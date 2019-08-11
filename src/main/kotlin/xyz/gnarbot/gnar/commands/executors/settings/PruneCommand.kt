package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.api.Permission
import xyz.gnarbot.gnar.commands.*
import java.time.OffsetDateTime

@Command(
        aliases = ["prune", "purge", "delmessages", "delmsgs"],
        usage = "(2-100)",
        description = "Delete up to 100 messages."
)
@BotInfo(
        id = 57,
        category = Category.SETTINGS,
        scope = Scope.TEXT,
        permissions = [Permission.MANAGE_SERVER]
)
class PruneCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        if (!context.guild.selfMember.hasPermission(Permission.MESSAGE_MANAGE)) {
            context.send().error("The bot can not prune messages without the `Manage Message` permission.").queue()
            return
        }

        if (args.isEmpty()) {
            context.bot.commandDispatcher.sendHelp(context, info)
            return
        }

        val history = context.textChannel.history

        val amount = args[0].toIntOrNull()?.coerceIn(0, 100)
        if (amount == null) {
            context.send().error("Improper arguments supplies, must be a number between `2-100`.").queue()
            return
        }

        if (amount < 2) {
            context.send().error("You need to delete 2 or more messages to use this command.").queue()
            return
        }

        val time = OffsetDateTime.now().minusWeeks(2)

        history.retrievePast(amount).queue {
            val messages = it.filter { msg ->
                msg.timeCreated.isAfter(time)
            }

            when {
                messages.size >= 2 -> {
                    context.message.textChannel.deleteMessages(messages).queue()
                    context.send().info("Attempted to delete **${messages.size}** messages.").queue()
                }
                messages.size == 1 -> {
                    messages.first().delete().queue()
                    context.send().info("Attempted to delete **1** messages.").queue()
                }
                else -> {
                    context.send().info("No messages were found (that are younger than 2 weeks).").queue()
                }
            }
        }
    }
}
