package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.commands.BotInfo
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Context

@Command(
        aliases = ["play", "skip", "queue", "remove", "repeat", "np", "restart", "shuffle", "volume", "voteskip", "dfm"],
        description = "Music is disabled TEMPORARILY."
)
@BotInfo(
        id = 62
)
class DisabledPlayCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        context.send().error("Music is disabled due to YouTube causing issues. Please stay tuned. https://discord.gg/NQRpmr2").queue()
    }
}