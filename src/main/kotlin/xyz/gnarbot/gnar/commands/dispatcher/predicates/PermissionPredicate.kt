package xyz.gnarbot.gnar.commands.dispatcher.predicates

import net.dv8tion.jda.api.Permission
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.hasAnyRoleNamed
import java.util.function.BiPredicate

class PermissionPredicate : BiPredicate<CommandExecutor, Context> {
    override fun test(cmd: CommandExecutor, context: Context): Boolean {
        if (context.member.hasPermission(Permission.ADMINISTRATOR)
                || !(cmd.botInfo.permissions.isNotEmpty() || cmd.botInfo.roleRequirement.isNotEmpty())) return true

        if (context.member.hasAnyRoleNamed(cmd.botInfo.roleRequirement)
                && cmd.botInfo.scope.checkPermission(context, *cmd.botInfo.permissions)) {
            return true
        }

        context.send().error(buildString {
            append("This command requires ")

            val permissionNotEmpty = cmd.botInfo.permissions.isNotEmpty()

            if (cmd.botInfo.roleRequirement.isNotEmpty()) {
                append("a role named `")
                append(cmd.botInfo.roleRequirement)
                append('`')

                if (permissionNotEmpty) {
                    append(" and ")
                }
            }

            if (permissionNotEmpty) {
                append("the permissions `")
                append(cmd.botInfo.permissions.map(Permission::getName))
                append("` in ")

                when (cmd.botInfo.scope) {
                    Scope.GUILD -> {
                        append("the guild `")
                        append(context.guild.name)
                    }
                    Scope.TEXT -> {
                        append("the text channel `")
                        append(context.textChannel.name)
                    }
                    Scope.VOICE -> {
                        append("the voice channel `")
                        append(context.voiceChannel.name)
                    }
                }
            }

            append("`.")
        }).queue()
        return false
    }
}