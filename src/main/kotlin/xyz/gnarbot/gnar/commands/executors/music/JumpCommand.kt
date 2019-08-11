package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.annotations.Description
import xyz.gnarbot.gnar.utils.Utils
import java.time.Duration

@Command(
        aliases = ["jump", "seek"],
        usage = "(to|forward|backward) (time)",
        description = "Set the time marker of the music playback."
)
@BotInfo(
        id = 65,
        category = Category.MUSIC,
        scope = Scope.VOICE
)
class JumpCommand : CommandTemplate() {
    @Description("Set the time marker of the player.")
    fun to(context: Context, duration: Duration) {
        val manager = context.bot.players.getExisting(context.guild)!!

        manager.player.playingTrack.position = duration.toMillis().coerceIn(0, manager.player.playingTrack.duration)

        context.send().info("The position of the track has been set to ${Utils.getTimestamp(manager.player.playingTrack.position)}.").queue()
    }

    @Description("Move the time marker forward.")
    fun forward(context: Context, duration: Duration) {
        val manager = context.bot.players.getExisting(context.guild)!!

        manager.player.playingTrack.position = (manager.player.playingTrack.position + duration.toMillis())
                .coerceIn(0, manager.player.playingTrack.duration)

        context.send().info("The position of the track has been set to ${Utils.getTimestamp(manager.player.playingTrack.position)}.").queue()
    }

    @Description("Move the time marker backward.")
    fun backward(context: Context, duration: Duration) {
        val manager = context.bot.players.getExisting(context.guild)!!

        manager.player.playingTrack.position = (manager.player.playingTrack.position - duration.toMillis())
                .coerceIn(0, manager.player.playingTrack.duration)

        context.send().embed("Jump Backward") {
            desc { "The position of the track has been set to ${Utils.getTimestamp(manager.player.playingTrack.position)}." }
        }.action().queue()
    }

    override fun execute(context: Context, label: String, args: Array<out String>) {
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

        if (context.voiceChannel != botChannel) {
            context.send().error("You're not in the same channel as the context.bot.").queue()
            return
        }

        if (manager.player.playingTrack == null) {
            context.send().error("The player is not playing anything.").queue()
            return
        }

        if (!manager.player.playingTrack.isSeekable) {
            context.send().error("You can't change the time marker on this track.").queue()
            return
        }

        super.execute(context, label, args)
    }
}
