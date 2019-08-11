package xyz.gnarbot.gnar.commands.executors.admin

import net.dv8tion.jda.api.JDA
import xyz.gnarbot.gnar.commands.*

@Command(
        aliases = ["revive", "restartShards"],
        description = "Restart all shard instances."
)
@BotInfo(
        id = 37,
        admin = true,
        category = Category.NONE
)
class RestartShardsCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Try `all, dead, (#)`").queue()
            return
        }

        when (args[0]) {
            "all" -> {
                context.send().info("Bot is now restarting.").queue()
                context.bot.restart()
            }
            "dead" -> {
                val deadShards = context.bot.shardManager.shardCache.filter { it.status != JDA.Status.CONNECTED }

                if (deadShards.isEmpty()) {
                    context.send().info("Every shard is connected.").queue()
                    return
                }

                context.send().info("Shards `${deadShards.map { it.shardInfo.shardId }}` are now restarting.").queue()
                deadShards.map { it.shardInfo.shardId }.forEach { context.bot.shardManager.restart(it) }
            }
            else -> {
                val id = args[0].toLongOrNull()?.coerceIn(0, context.bot.shardManager.shardCache.size())?.toInt() ?: kotlin.run {
                    context.send().error("You must enter a valid shard id.").queue()
                    return
                }

                context.send().info("Shards `$id` are now restarting.").queue()
                context.bot.shardManager.restart(id)
            }
        }
    }
}