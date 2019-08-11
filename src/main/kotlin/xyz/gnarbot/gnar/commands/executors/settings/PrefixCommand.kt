package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.api.Permission
import xyz.gnarbot.gnar.commands.BotInfo
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.annotations.Description

@Command(
        aliases = ["prefix"],
        usage = "(set|reset) [string]",
        description = "Set the bot prefix for the server."
)
@BotInfo(
        id = 56,
        category = Category.SETTINGS,
        permissions = [Permission.MANAGE_SERVER]
)
class PrefixCommand : CommandTemplate() {
    private val mention = Regex("<@!?(\\d+)>|<#(\\d+)>|<@&(\\d+)>")

    @Description("Set the prefix.")
    fun set(context: Context, prefix: String) {
        if (prefix matches mention) {
            context.send().error("The prefix can't be set to a mention.").queue()
            return
        }

        if (context.data.command.prefix == prefix) {
            context.send().error("The prefix is already set to `$prefix`.").queue()
            return
        }

        context.data.command.prefix = prefix
        context.data.save()

        context.send().info("The prefix has been set to `${context.data.command.prefix}`.").queue()
    }

    @Description("Reset to the default prefix.")
    fun reset(context: Context) {
        if (context.data.command.prefix == null) {
            context.send().error("The prefix is already set to the default.").queue()
            return
        }

        context.data.command.prefix = null
        context.data.save()

        context.send().info("The prefix has been reset to `${context.bot.configuration.prefix}`.").queue()
    }

    override fun onWalkFail(context: Context, args: Array<String>, depth: Int) {
        onWalkFail(context, args, depth, null, buildString {
            append("Default prefix will still be valid.\n")
            val prefix = context.data.command.prefix
            append("Current prefix: `").append(prefix).append('`')
        })
    }
}