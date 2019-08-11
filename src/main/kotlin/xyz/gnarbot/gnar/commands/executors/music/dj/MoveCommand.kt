package xyz.gnarbot.gnar.commands.executors.music.dj

import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.commands.executors.music.MusicCommandExecutor
import xyz.gnarbot.gnar.commands.template.parser.Parsers
import xyz.gnarbot.gnar.music.MusicManager

@Command(
        aliases = ["move"],
        description = "Move the bot to another channel."
)
@BotInfo(
        id = 77,
        category = Category.MUSIC,
        scope = Scope.VOICE,
        roleRequirement = "DJ"
)
class MoveCommand : MusicCommandExecutor(false, false) {
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        val targetChannel = if (args.isEmpty()) {
            context.voiceChannel
        } else {
            Parsers.VOICE_CHANNEL.parse(context, args.joinToString(" "))
        }

        if (targetChannel == null) {
            context.send().error("That's not a valid music channel.").queue()
            return
        }

        if (targetChannel == context.selfMember.voiceState?.channel) {
            context.send().error("That's the same channel.").queue()
            return
        }

        if (context.data.music.channels.isNotEmpty()) {
            if (targetChannel.id !in context.data.music.channels) {
                context.send().error("Can not join `${targetChannel.name}`, it isn't one of the designated music channels.").queue()
                return
            }
        }

        context.guild.audioManager.openAudioConnection(targetChannel)
        // assume magic from VoiceListener.kt
    }
}
