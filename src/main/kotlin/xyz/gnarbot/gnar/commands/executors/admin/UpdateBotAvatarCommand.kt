package xyz.gnarbot.gnar.commands.executors.admin

import net.dv8tion.jda.api.entities.Icon
import xyz.gnarbot.gnar.commands.*
import java.net.URL
import kotlin.use

@Command(
        aliases = ["botAvatar"]
)
@BotInfo(
        id = 36,
        category = Category.NONE,
        admin = true
)
class UpdateBotAvatarCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        if (!args.isEmpty()) {
            URL(args.joinToString(" ")).openStream().use {
                context.bot.shardManager.shards[0].selfUser.manager.setAvatar(Icon.from(it)).queue()
            }
        }
    }
}
