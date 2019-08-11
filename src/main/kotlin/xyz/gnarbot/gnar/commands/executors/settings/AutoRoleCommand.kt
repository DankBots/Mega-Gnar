package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Role
import xyz.gnarbot.gnar.commands.BotInfo
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.annotations.Description

@Command(
        aliases = ["autorole"],
        usage = "(set|unset) [@role]",
        description = "Set auto-roles that are assigned to users on joining."
)
@BotInfo(
        id = 52,
        category = Category.SETTINGS,
        permissions = [Permission.MANAGE_ROLES]
)
class AutoRoleCommand : CommandTemplate() {
    @Description("Set the auto-role.")
    fun set(context: Context, role: Role) {
        if (!context.selfMember.hasPermission(Permission.MANAGE_ROLES)) {
            context.send().error("The bot can not manage auto-roles without the ${Permission.MANAGE_ROLES.getName()} permission.").queue()
            return
        }

        if (role == context.guild.publicRole) {
            context.send().error("You can't grant the public role!").queue()
            return
        }

        if (!context.selfMember.canInteract(role)) {
            context.send().error("That role is higher than my role! Fix by changing the role hierarchy.").queue()
            return
        }

        if (role.id == context.data.roles.autoRole) {
            context.send().error("${role.asMention} is already set as the auto-role.").queue()
            return
        }


        context.data.roles.autoRole = role.id
        context.data.save()

        context.send().info("Users joining the guild will now be granted the role ${role.asMention}.").queue()
    }

    @Description("Unset the auto-role.")
    fun reset(context: Context) {
        if (context.data.roles.autoRole == null) {
            context.send().error("This guild doesn't have an auto-role.").queue()
            return
        }

        context.data.roles.autoRole = null
        context.data.save()

        context.send().info("Unset autorole. Users joining the guild will not be granted any role.").queue()
    }

    override fun onWalkFail(context: Context, args: Array<String>, depth: Int) {
        onWalkFail(context, args, depth, null, buildString {
            if (!context.selfMember.hasPermission(Permission.MANAGE_ROLES)) {
                append("**WARNING:** Bot lacks the ${Permission.MANAGE_ROLES.getName()} permission.")
                return
            }

            val role = context.data.roles.autoRole
            append("Current auto-role: ")
            if (role == null) {
                append("__None__")
            } else {
                context.guild.getRoleById(role)?.asMention?.let { append(it) }
            }
        })
    }
}