package xyz.gnarbot.gnar.commands.executors.general

import net.dv8tion.jda.api.JDAInfo
import xyz.gnarbot.gnar.commands.BotInfo
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Context
import java.lang.management.ManagementFactory

@Command(
        aliases = ["about", "info", "botinfo"],
        description = "Show information about the context.bot."
)
@BotInfo(
        id = 42
)
class BotInfoCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        val registry = context.bot.commandRegistry

        // Uptime
        val s = ManagementFactory.getRuntimeMXBean().uptime / 1000
        val m = s / 60
        val h = m / 60
        val d = h / 24

//        var requests = 0L
        var textChannels = 0L
        var voiceChannels = 0L
        var guilds = 0L

        var users = 0L

        for (shard in context.bot.shardManager.shardCache) {
            guilds += shard.guildCache.size()
            users += shard.userCache.size()
            textChannels += shard.textChannelCache.size()
            voiceChannels += shard.voiceChannelCache.size()
        }

        val commandSize = registry.entries.count { it.botInfo.category.show }

        context.send().embed("Bot Information") {
            thumbnail { context.jda.selfUser.avatarUrl }
            desc { "Gnar is a music bot packed with dank memes to rescue your soul from the depths of the underworld." }

//            field("Session Requests", true) { requests }
//            field("Requests Per Hour", true) { requests / Math.max(1, h) }

            field("Text Channels", true) { textChannels }
            field("Voice Channels", true) { voiceChannels }

            field("Guilds", true) { guilds }
            field("Voice Connections", true) { context.bot.players.size() }

            field("Users", true) { users }
            field("Uptime", true) { "${d}d ${h % 24}h ${m % 60}m ${s % 60}s" }

            field("General", true) {
                buildString {
                    append("Donations: **[Patreon](https://gnarbot.xyz/donate)**\n")
                    append("Commands: **$commandSize**\n")
                    append("Library: **[JDA ${JDAInfo.VERSION}](${JDAInfo.GITHUB})**\n")
                }
            }
            field("Credits", true) {
                buildString {
                    append("The Serious: **[Avarel](https://github.com/Avarel)**\n")
                    append("The Sarcastic: **[Xevryll](https://github.com/xevryll)**\n")
                    append("Contributor: **[Gatt](https://github.com/RealGatt)**\n")
                }
            }
        }.action().queue()
    }
}
