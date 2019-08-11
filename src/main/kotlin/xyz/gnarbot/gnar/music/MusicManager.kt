package xyz.gnarbot.gnar.music

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioTrack
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioTrack
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.VoiceChannel
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.commands.executors.music.embedTitle
import xyz.gnarbot.gnar.utils.response.respond
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class MusicManager(private val bot: Bot, val guild: Guild, val playerRegistry: PlayerRegistry) {
    companion object {
        val playerManager: AudioPlayerManager = DefaultAudioPlayerManager().also {
            it.registerSourceManager(YoutubeAudioSourceManager().apply {
                configureRequests {
                    RequestConfig.copy(it).setCookieSpec(CookieSpecs.IGNORE_COOKIES).build()
                }
            })
            it.registerSourceManager(SoundCloudAudioSourceManager())
            it.registerSourceManager(BandcampAudioSourceManager())
            it.registerSourceManager(VimeoAudioSourceManager())
            it.registerSourceManager(TwitchStreamAudioSourceManager())
            it.registerSourceManager(BeamAudioSourceManager())
        }

        fun search(query: String, maxResults: Int = -1, callback: (results: List<AudioTrack>) -> Unit) {
            playerManager.loadItem(query, object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {
                    callback(listOf(track))
                }

                override fun playlistLoaded(playlist: AudioPlaylist) {
                    if (!playlist.isSearchResult) {
                        return
                    }

                    if (maxResults == -1) {
                        callback(playlist.tracks)
                    } else {
                        callback(playlist.tracks.subList(0, Math.min(maxResults, playlist.tracks.size)))
                    }
                }

                override fun noMatches() {
                    callback(emptyList())
                }

                override fun loadFailed(e: FriendlyException) {
                    callback(emptyList())
                }
            })
        }
    }

    @Volatile
    private var leaveTask: Future<*>? = null

    private var equalizer: EqualizerFactory = EqualizerFactory()

    private var equalizerEnabled = false

    /** @return Audio player for the guild. */
    val player: AudioPlayer = playerManager.createPlayer().also {
        // fixme DI point
        it.volume = bot.options.ofGuild(guild).music.volume
    }

    /**  @return Track scheduler for the player.*/
    val scheduler: TrackScheduler = TrackScheduler(bot, this, player).also(player::addListener)

    /** @return Wrapper around AudioPlayer to use it as an AudioSendHandler. */
    private val sendHandler: AudioPlayerSendHandler = AudioPlayerSendHandler(player)

    /**
     * @return Voting cooldown.
     */
    var lastVoteTime: Long = 0L

    /**
     * @return Whether there is a vote to skip the song or not.
     */
    var isVotingToSkip = false

    val currentRequestChannel: TextChannel?
        get() {
            return (player.playingTrack ?: scheduler.lastTrack)
                    ?.getUserData(TrackContext::class.java)
                    ?.requestedChannel
                    ?.let(guild::getTextChannelById)
        }

    /**
     * @return If the user is listening to DiscordFM
     */
    var discordFMTrack : DiscordFMTrackContext? = null

    fun destroy() {
        player.destroy()
        disableBass()
        closeAudioConnection()
    }

    fun openAudioConnection(channel: VoiceChannel, context: Context) : Boolean {
        when {
            !bot.configuration.musicEnabled -> {
                context.send().error("Music is disabled.").queue()
                playerRegistry.destroy(guild)
                return false
            }
            !guild.selfMember.hasPermission(channel, Permission.VOICE_CONNECT) -> {
                context.send().error("The bot can't connect to this channel due to a lack of permission.").queue()
                playerRegistry.destroy(guild)
                return false
            }
            channel.userLimit != 0
                    && guild.selfMember.hasPermission(channel, Permission.VOICE_MOVE_OTHERS)
                    && channel.members.size >= channel.userLimit -> {
                context.send().error("The bot can't join due to the user limit.").queue()
                playerRegistry.destroy(guild)
                return false
            }
            else -> {
                guild.audioManager.sendingHandler = sendHandler
                guild.audioManager.openAudioConnection(channel)

                context.send().embed("Music Playback") {
                    desc { "Joining channel `${channel.name}`." }
                }.action().queue()
                return true
            }
        }
    }

    fun moveAudioConnection(channel: VoiceChannel) {
        guild.let {
            if (!guild.selfMember.voiceState!!.inVoiceChannel()) {
                throw IllegalStateException("Bot is not in a voice channel")
            }

            if (!guild.selfMember.hasPermission(channel, Permission.VOICE_CONNECT)) {
                currentRequestChannel?.respond()?.error("I don't have permission to join `${channel.name}`.")?.queue()
                playerRegistry.destroy(guild)
                return
            }

            player.isPaused = true
            it.audioManager.openAudioConnection(channel)
            player.isPaused = false

            currentRequestChannel?.respond()?.embed("Music Playback") {
                desc { "Moving to channel `${channel.name}`." }
            }?.action()?.queue()
        }
    }

    fun closeAudioConnection() {
        guild.let {
            it.audioManager.closeAudioConnection()
            it.audioManager.sendingHandler = null
        }
    }

    fun isAlone(): Boolean {
        return guild.selfMember.voiceState!!.channel?.members?.let {
            it.size == 1 && it[0] == guild.selfMember
        } != false
    }

    fun queueLeave() {
        leaveTask?.cancel(false)
        leaveTask = createLeaveTask()
        player.isPaused = true
    }

    fun cancelLeave() {
        leaveTask?.cancel(false)
        leaveTask = null
        player.isPaused = false
    }

    private fun createLeaveTask() = playerRegistry.executor.schedule({
        playerRegistry.destroy(guild)
    }, 30, TimeUnit.SECONDS)

    fun loadAndPlay(context: Context, trackUrl: String, trackContext: TrackContext, footnote: String? = null) {
        playerManager.loadItemOrdered(this, trackUrl, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                if (!guild.selfMember.voiceState!!.inVoiceChannel()) {
                    if (!openAudioConnection(context.voiceChannel, context)) {
                        return
                    }
                }

                if (scheduler.queue.size >= bot.configuration.queueLimit) {
                    context.send().error("The queue can not exceed ${bot.configuration.queueLimit} songs.").queue()
                    return
                }

                if (track !is TwitchStreamAudioTrack && track !is BeamAudioTrack) {
                    if (track.duration > bot.configuration.durationLimit.toMillis()) {
                        context.send().error("The track can not exceed ${bot.configuration.durationLimitText}.").queue()
                        return
                    }
                }

                track.userData = trackContext

                scheduler.queue(track)

                context.send().embed("Music Queue") {
                    desc { "Added __**[${track.info.embedTitle}](${track.info.uri})**__ to queue." }
                    footer { footnote }
                }.action().queue()
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                if (playlist.isSearchResult) {
                    trackLoaded(playlist.tracks.first())
                    return
                }

                if (!guild.selfMember.voiceState!!.inVoiceChannel()) {
                    if (!context.member.voiceState!!.inVoiceChannel()) {
                        context.send().error("You left the channel before the track is loaded.").queue()

                        // Track is not supposed to load and the queue is empty
                        // destroy player
                        if (scheduler.queue.isEmpty()) {
                            playerRegistry.destroy(guild)
                        }
                        return
                    }
                    if (!openAudioConnection(context.voiceChannel, context)) {
                        return
                    }
                }

                val tracks = playlist.tracks
                var ignored = 0

                var added = 0
                for (track in tracks) {
                    if (scheduler.queue.size + 1 >= bot.configuration.queueLimit) {
                        ignored = tracks.size - added
                        break
                    }

                    track.userData = trackContext

                    scheduler.queue(track)
                    added++
                }

                context.send().embed("Music Queue") {
                    desc {
                        buildString {
                            append("Added `$added` tracks to queue from playlist `${playlist.name}`.\n")
                            if (ignored > 0) {
                                append("Ignored `$ignored` songs as the queue can not exceed `${bot.configuration.queueLimit}` songs.")
                            }
                        }
                    }

                    footer { footnote }
                }.action().queue()
            }

            override fun noMatches() {
                // No track found and queue is empty
                // destroy player
                if (scheduler.queue.isEmpty()) {
                    playerRegistry.destroy(guild)
                }
                context.send().error("Nothing found by `$trackUrl`.").queue()
            }

            override fun loadFailed(e: FriendlyException) {
                // No track found and queue is empty
                // destroy player

                if(e.message!!.contains("decoding")) {
                    return
                }

                if (scheduler.queue.isEmpty()) {
                    playerRegistry.destroy(guild)
                }
                context.send().exception(e).queue()
            }
        })
    }

    //Credit to JukeBot and CircuitCRay
    fun boostBass(b1: Float, b2: Float) {
        equalizer.setGain(0, b1)
        equalizer.setGain(1, b2)

        if(!equalizerEnabled) {
            player.setFilterFactory(equalizer)
            equalizerEnabled = true
        }
    }

    fun disableBass() {
        if(equalizerEnabled) {
            equalizer.setGain(0, 0F)
            equalizer.setGain(1, 0F)
            player.setFilterFactory(null)
            equalizerEnabled = false
        }
    }
}
