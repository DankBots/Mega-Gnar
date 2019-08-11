package xyz.gnarbot.gnar.commands.dispatcher.predicates

import net.dv8tion.jda.api.entities.VoiceChannel
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.commands.Scope
import java.util.function.BiPredicate

class VoiceStatePredicate : BiPredicate<CommandExecutor, Context> {
    override fun test(cmd: CommandExecutor, context: Context): Boolean {
        if (cmd.botInfo.scope != Scope.VOICE) return true

        if (context.member.voiceState?.channel == null) {
            context.send().error("\uD83C\uDFB6 Music commands requires you to be in a voice channel.").queue()
            return false
        } else if (context.member.voiceState?.channel == context.guild.afkChannel) {
            context.send().error("Music can't be played in the AFK channel.").queue()
            return false
        } else if (context.data.music.channels.isNotEmpty()
                && context.member.voiceState?.channel?.id !in context.data.music.channels) {

            val channels = context.data.music.channels
                    .mapNotNull(context.guild::getVoiceChannelById)
                    .map(VoiceChannel::getName)

            context.send().error("Music can only be played in: `$channels`.").queue()
            return false
        }

        return true
    }
}
