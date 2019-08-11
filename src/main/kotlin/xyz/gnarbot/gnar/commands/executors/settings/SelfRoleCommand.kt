package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Role
import xyz.gnarbot.gnar.commands.BotInfo
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.annotations.Description

@Command(
        aliases = ["selfroles", "selfrole"],
        usage = "(add|remove|clear) [@role]",
        description = "Set self-roles that users can assign to themselves."
)
@BotInfo(
        id = 59,
        category = Category.SETTINGS,
        permissions = [Permission.MANAGE_ROLES]
)
class SelfRoleCommand : CommandTemplate() {
    @Description("Add a self-role.")
    fun add(context: Context, role: Role) {
        if (!context.selfMember.hasPermission(Permission.MANAGE_ROLES)) {
            context.send().error("The bot can not  needs the ${Permission.MANAGE_ROLES.getName()} permission.").queue()
            return
        }

        if (role == context.guild.publicRole) {
            context.send().error("You can't add the public role!").queue()
            return
        }

        if (!context.selfMember.canInteract(role)) {
            context.send().error("That role is higher than my role! Fix by changing the role hierarchy.").queue()
            return
        }

        if (role.id in context.data.roles.selfRoles) {
            context.send().error("${role.asMention} is already added as a self-assignable role.").queue()
            return
        }

        context.data.roles.selfRoles.add(role.id)
        context.data.save()

        context.send().info("Added ${role.asMention} to the list of self-assignable roles. Users can get them using `_iam`.").queue()
    }

    @Description("Remove a self-role.")
    fun remove(context: Context, role: Role) {
        if (role.id !in context.data.roles.selfRoles) {
            context.send().error("${role.asMention} is not a self-assignable role.").queue()
            return
        }

        context.data.roles.selfRoles.remove(role.id)
        context.data.save()

        context.send().info("Removed ${role.asMention} from the list of self-assignable roles.").queue()
    }

    @Description("Clear all self-assignable roles.")
    fun clear(context: Context) {
        if (context.data.roles.selfRoles.isEmpty()) {
            context.send().error("This guild doesn't have any self-assignable roles.").queue()
            return
        }

        context.data.roles.selfRoles.clear()
        context.data.save()

        context.send().info("Cleared the list of self-assignable roles.").queue()
    }

    @Description("List self-assignable roles.")
    fun list(context: Context) {
        context.send().embed("Self-Roles") {
            desc {
                if (context.data.roles.selfRoles.isEmpty()) {
                    "This guild doesn't have any self-assignable roles."
                } else {
                    buildString {
                        if (!context.selfMember.hasPermission(Permission.MANAGE_ROLES)) {
                            append("**WARNING:** Bot lacks the ${Permission.MANAGE_ROLES.getName()} permission.")
                            return
                        }

                        context.data.roles.selfRoles.mapNotNull(context.guild::getRoleById)
                                .map(IMentionable::getAsMention)
                                .forEach { append("• ").append(it).append('\n') }
                    }
                }
            }
        }.action().queue()
    }
}