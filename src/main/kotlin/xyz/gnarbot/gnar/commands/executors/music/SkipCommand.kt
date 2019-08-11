package xyz.gnarbot.gnar.commands.executors.music

import net.dv8tion.jda.api.Permission
import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.music.TrackContext
import xyz.gnarbot.gnar.utils.hasAnyRoleNamed

@Command(
        aliases = ["skip"],
        description = "Skip the current music track if you're the requester."
)
@BotInfo(
        id = 73,
        category = Category.MUSIC,
        scope = Scope.VOICE
)
class SkipCommand : MusicCommandExecutor(true, true) {
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        if (!(context.member.hasAnyRoleNamed("DJ") || context.member.hasPermission(Permission.ADMINISTRATOR)
                || manager.player.playingTrack.getUserData(TrackContext::class.java)?.requester == context.user.idLong)) {
            context.send().error("You did not request this track.").queue()
            return
        }

        manager.scheduler.nextTrack()

        context.send().info("The track was skipped.").queue()
    }
}
