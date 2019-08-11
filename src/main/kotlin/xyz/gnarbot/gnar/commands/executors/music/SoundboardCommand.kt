package xyz.gnarbot.gnar.commands.executors.music

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import org.apache.commons.lang3.StringUtils
import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicLimitException
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.music.TrackContext

@Command(
        aliases = ["playm", "sb", "sounds"],
        description = "Plays the meme requested"
)
@BotInfo(
        id = 86,
        category = Category.MUSIC,
        scope = Scope.VOICE,
        donor = true
)
class SoundboardCommand : CommandExecutor() {
    private val footnote = "You can try to play a meme by using _sb"

    override fun execute(context: Context, label: String, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Valid options are `${context.bot.soundManager.map.keys.toTypedArray()}`").queue()
            return
        }

        val manager = context.bot.players.getExisting(context.guild)

        if (manager != null) {
            if (manager.scheduler.queue.size > 0) {
                context.send().error("Only one meme at a time please meme lord!").queue()
                return
            }
        }


        var distance = 999
        var meme = ""
        for (s in context.bot.soundManager.map.keys) {
            val dist = StringUtils.getLevenshteinDistance(s, args.joinToString())
            if(dist < distance) {
                distance = dist
                meme = context.bot.soundManager.map[s]!!
            }
        }

        AudioSourceManagers.registerLocalSource(MusicManager.playerManager)
        AudioSourceManagers.registerRemoteSources(MusicManager.playerManager)

        MusicManager.search(meme, 1) { results ->
            if (results.isEmpty()) {
                context.send().error("Dank Meme not found, NANI?").queue()
                return@search
            }

            val result = results[0]

            val manager = try {
                context.bot.players.get(context.guild)
            } catch (e: MusicLimitException) {
                e.sendToContext(context)
                return@search
            }

            manager.loadAndPlay(
                    context,
                    result.info.uri,
                    TrackContext(
                            context.member.user.idLong,
                            context.textChannel.idLong
                    ),
                    footnote
            )
        }

    }
}