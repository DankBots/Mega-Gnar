package xyz.gnarbot.gnar.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.executors.music.embedTitle
import xyz.gnarbot.gnar.utils.response.respond
import java.util.*

class TrackScheduler(private val bot: Bot, private val manager: MusicManager, private val player: AudioPlayer) : AudioEventAdapter() {
    val queue: Queue<AudioTrack> = LinkedList()
    var repeatOption = RepeatOption.NONE
    var lastTrack: AudioTrack? = null
        private set

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    fun queue(track: AudioTrack) {
        if (!player.startTrack(track, true)) {
            queue.offer(track)
        }
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    fun nextTrack() {
        if (queue.isEmpty()) {
            manager.discordFMTrack?.let {
                it.nextDiscordFMTrack(manager)
                return
            }

            manager.playerRegistry.executor.execute { manager.playerRegistry.destroy(manager.guild) }
            return
        }

        val track = queue.poll()
        player.startTrack(track, false)

        // fixme DI point
        if (bot.options.ofGuild(manager.guild).music.announce) {
            announceNext(track)
        }
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        this.lastTrack = track

        if (endReason.mayStartNext) {
            when (repeatOption) {
                RepeatOption.SONG -> {
                    val newTrack = track.makeClone().also { it.userData = track.userData }
                    player.startTrack(newTrack, false)
                }
                RepeatOption.QUEUE -> {
                    val newTrack = track.makeClone().also { it.userData = track.userData }
                    queue.offer(newTrack)
                    nextTrack()
                }
                RepeatOption.NONE -> nextTrack()
            }
        }
    }

    override fun onTrackStuck(player: AudioPlayer, track: AudioTrack, thresholdMs: Long) {
        track.getUserData(TrackContext::class.java).requestedChannel.let {
            manager.guild.getTextChannelById(it)
        }?.respond()?.error(
                "The track ${track.info.embedTitle} is stuck longer than ${thresholdMs}ms threshold."
        )?.queue()
    }

    override fun onTrackException(player: AudioPlayer, track: AudioTrack, exception: FriendlyException) {
        if(exception.toString().contains("decoding")) {
            return
        }
        track.getUserData(TrackContext::class.java).requestedChannel.let {
            manager.guild.getTextChannelById(it)
        }?.respond()?.exception(exception)?.queue()
    }

    private fun announceNext(track: AudioTrack) {
        manager.currentRequestChannel?.let {
            it.respond().embed("Music Playback") {
                desc {
                    buildString {
                        append("Now playing __**[").append(track.info.embedTitle)
                        append("](").append(track.info.uri).append(")**__")

                        track.getUserData(TrackContext::class.java)?.requester?.let(manager.guild::getMemberById)?.let {
                            append(" requested by ")
                            append(it.asMention)
                        }

                        append(".")
                    }
                }
            }.action().queue()
        }
    }

    fun shuffle() = (queue as MutableList<*>).shuffle()
}
