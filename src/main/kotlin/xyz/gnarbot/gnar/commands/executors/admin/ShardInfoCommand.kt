package xyz.gnarbot.gnar.commands.executors.admin

import com.google.common.collect.Lists
import xyz.gnarbot.gnar.commands.*
import java.util.*

@Command(
        aliases = ["shards", "shard", "shardinfo"],
        description = "Get shard information."
)
@BotInfo(
        id = 48,
        admin = true,
        category = Category.NONE
)
class ShardInfoCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        context.send().text("```prolog\n ID |    STATUS |    PING | GUILDS |  USERS | REQUESTS |  VC\n```").queue()

        Lists.partition(context.bot.shardManager.shards, 20).forEach {
            val joiner = StringJoiner("\n", "```prolog\n", "```")

            it.forEach {
                joiner.add(
                        "%3d | %9.9s | %7.7s | %6d | %6d | ---- WIP | %3d".format(
                                it.shardInfo.shardId,
                                it.status,
                                "${it.gatewayPing}ms",
                                it.guildCache.size(),
                                it.userCache.size(),
                                context.bot.players.registry.values.count { m -> m.guild.jda == it }
                        )
                )
            }

            context.send().text(joiner.toString()).queue()
        }
    }

}