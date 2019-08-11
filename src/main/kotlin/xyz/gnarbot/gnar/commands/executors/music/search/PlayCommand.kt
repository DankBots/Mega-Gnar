package xyz.gnarbot.gnar.commands.executors.music.search

import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicLimitException
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.music.TrackContext

@Command(
        aliases = ["play"],
        usage = "[url|YT search]",
        description = "Joins and play music in a channel."
)
@BotInfo(
        id = 62,
        scope = Scope.VOICE,
        category = Category.MUSIC
)
class PlayCommand : CommandExecutor() {
    private val footnote = "You can search and pick results using _youtube or _soundcloud while in a channel."

    override fun execute(context: Context, label: String, args: Array<String>) {
        val botChannel = context.selfMember.voiceState?.channel
        val userChannel = context.voiceChannel

        if (botChannel != null && botChannel != userChannel) {
            context.send().error("The bot is already playing music in another channel.").queue()
            return
        }

        if (args.isEmpty()) {
            val manager = context.bot.players.getExisting(context.guild)
            if (manager == null) {
                context.send().error("There's no music player in this guild.\n" +
                        "\uD83C\uDFB6` _play (song/url)` to start playing some music!").queue()
                return
            }

            when {
                manager.player.isPaused -> {
                    manager.player.isPaused = false
                    context.send().embed("Play Music") {
                        desc { "Music is now playing." }
                    }.action().queue()
                }
                manager.player.playingTrack != null -> {
                    context.send().error("Music is already playing.").queue()
                }
                manager.scheduler.queue.isEmpty() -> {
                    context.send().embed("Empty Queue") {
                        desc { "There is no music queued right now. Add some songs with `play -song|url`." }
                    }.action().queue()
                }
            }
            return
        }

        if ("https://" in args[0] || "http://" in args[0]) {
            val manager = try {
                context.bot.players.get(context.guild)
            } catch (e: MusicLimitException) {
                e.sendToContext(context)
                return
            }

            manager.loadAndPlay(
                    context,
                    args[0],
                    TrackContext(
                            context.member.user.idLong,
                            context.textChannel.idLong
                    ),
                    footnote
            )
        } else {
            if (!context.bot.configuration.searchEnabled) {
                context.send().error("Search is currently disabled. Try direct links instead.").queue()
                return
            }

            val query = args.joinToString(" ").trim()

//            if (query.startsWith("ytsearch:", ignoreCase = true)) {
//                Bot.getCommandRegistry().getCommand("youtube").execute(context, arrayOf(query.replaceFirst("ytsearch:", "", true)))
//                return
//            } else if (query.startsWith("scsearch:", ignoreCase = true)) {
//                Bot.getCommandRegistry().getCommand("soundcloud").execute(context, arrayOf(query.replaceFirst("scsearch:", "", true)))
//                return
//            } else if (query.startsWith("youtube ", ignoreCase = true)) {
//                Bot.getCommandRegistry().getCommand("youtube").execute(context, arrayOf(query.replaceFirst("youtube ", "", true)))
//                return
//            } else if (query.startsWith("soundcloud ", ignoreCase = true)) {
//                Bot.getCommandRegistry().getCommand("soundcloud").execute(context, arrayOf(query.replaceFirst("soundcloud ", "", true)))
//                return
//            }

            MusicManager.search("ytsearch:$query", 1) { results ->
                if (results.isEmpty()) {
                    context.send().error("No YouTube results returned for `${query.replace('+', ' ')}`.").queue()
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
}
