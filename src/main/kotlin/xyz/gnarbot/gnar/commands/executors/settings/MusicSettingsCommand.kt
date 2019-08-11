package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.VoiceChannel
import xyz.gnarbot.gnar.commands.BotInfo
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.annotations.Description

@Command(
        aliases = ["musicsettings", "musicconfig"],
        description = "Change music settings."
)
@BotInfo(
        id = 55,
        category = Category.SETTINGS,
        permissions = [Permission.MANAGE_SERVER]
)
class MusicSettingsCommand : CommandTemplate() {
    @Description("Toggle music announcement.")
    fun toggle_announcements(context: Context) {
        val value = context.data.music.announce
        context.data.music.announce = !value
        context.data.save()

        context.send().info(if (value) "Announcements for music disabled." else "Announcements for music enabled.").queue()
    }

    @Description("Add voice channels that Gnar can play music in.")
    fun voice_channel_add(context: Context, channel: VoiceChannel) {
        if (channel.id in context.data.music.channels) {
            context.send().error("`${channel.name}` is already a music channel.").queue()
            return
        }

        if (channel == context.guild.afkChannel) {
            context.send().error("`${channel.name}` is the AFK channel, you can't play music there.").queue()
            return
        }

        context.data.music.channels.add(channel.id)
        context.data.save()

        context.send().info("`${channel.name}` is now a designated music channel.").queue()
    }

    @Description("Remove voice channels that Gnar can play music in.")
    fun voice_channel_remove(context: Context, channel: VoiceChannel) {
        if (channel.id !in context.data.music.channels) {
            context.send().error("`${channel.name}` is not one of the music channels.").queue()
            return
        }

        context.data.music.channels.remove(channel.id)
        context.data.save()

        context.send().info("${channel.name} is no longer a designated music channel.").queue()
    }

    @Description("List all settings, their description and their values.")
    fun list(context: Context) {
        context.send().embed("Music Settings") {
            field("Announcements") {
                buildString {
                    append("Music announcements are __")
                    append(if (context.data.music.announce) "enabled" else "disabled")
                    append("__, toggle with `_music toggle announcements`.")
                }
            }

            field("Music Channels") {
                buildString {
                    append("If this is not empty, Gnar will only play music in these voice channels.\n\n")
                    context.data.music.channels.let {
                        if (it.isEmpty()) append("None.")

                        it.mapNotNull(context.guild::getVoiceChannelById)
                                .map(VoiceChannel::getName)
                                .forEach { append("• ").append(it).append('\n') }
                    }
                }
            }
        }.action().queue()
    }
}
