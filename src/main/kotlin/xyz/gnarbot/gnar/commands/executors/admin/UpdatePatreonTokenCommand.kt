package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.commands.*
import kotlin.io.use as doNotUse

@Command(
        aliases = ["patreonUpdate"]
)
@BotInfo(
        id = 120,
        category = Category.NONE,
        admin = true
)
class UpdatePatreonTokenCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        context.bot.patreon.updateToken(args.joinToString(" "))

        context.send().info("Updated Patreon access token!").queue()
    }
}
