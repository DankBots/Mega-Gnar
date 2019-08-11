package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.music.MusicManager

abstract class MusicCommandExecutor(private val sameChannel: Boolean, private val requirePlayingTrack: Boolean) : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        val manager = context.bot.players.getExisting(context.guild)
        if (manager == null) {
            context.send().error("There's no music player in this guild.\n$PLAY_MESSAGE").queue()
            return
        }

        val botChannel = context.selfMember.voiceState?.channel
        if (botChannel == null) {
            context.send().error("The bot is not currently in a channel.\n$PLAY_MESSAGE").queue()
            return
        }

        if (sameChannel && context.voiceChannel != botChannel) {
            context.send().error("You're not in the same channel as the context.bot.").queue()
            return
        }

        if (requirePlayingTrack && manager.player.playingTrack == null) {
            context.send().error("The player is not playing anything.").queue()
            return
        }

        execute(context, label, args, manager)
    }

    abstract fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager)
}