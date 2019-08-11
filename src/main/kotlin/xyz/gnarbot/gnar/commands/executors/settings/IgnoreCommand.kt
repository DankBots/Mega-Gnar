package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import xyz.gnarbot.gnar.commands.BotInfo
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.annotations.Description

@Command(
        aliases = ["ignore"],
        usage = "(user|channel|role|list) [?entity]",
        description = "Make the bot ignore certain users, channels or roles."
)
@BotInfo(
        id = 53,
        category = Category.SETTINGS,
        permissions = [Permission.MANAGE_SERVER]
)
class IgnoreCommand : CommandTemplate() {
    @Description("Ignore/unignore users.")
    fun user(context: Context, member: Member) {
        if (!context.member.canInteract(member)) {
            context.send().error("You can not interact with this user.").queue()
            return
        }

        context.data.ignored.users.let {
            if (it.contains(member.user.id)) {
                it.remove(member.user.id)

                context.send().info("No longer ignoring user ${member.asMention}.").queue()
            } else {
                it.add(member.user.id)

                context.send().info("Ignored user ${member.asMention}.").queue()
            }
        }
        context.data.save()
    }

    @Description("Ignore/unignore a channel.")
    fun channel(context: Context, channel: TextChannel) {
        context.data.ignored.channels.let {
            if (it.contains(channel.id)) {
                it.remove(channel.id)

                context.send().info("No longer ignoring channel ${channel.asMention}.").queue()
            } else {
                it.add(channel.id)

                context.send().info("Ignored channel ${channel.asMention}.").queue()
            }
        }
        context.data.save()
    }

    @Description("Ignore/unignore a role.")
    fun role(context: Context, role: Role) {
        if (role == context.guild.publicRole) {
            context.send().error("You can't ignore the public role!").queue()
            return
        }

        context.data.ignored.roles.let {
            if (it.contains(role.id)) {
                it.remove(role.id)

                context.send().info("No longer ignoring role ${role.asMention}.").queue()
            } else {
                it.add(role.id)

                context.send().info("Ignored role ${role.asMention}.").queue()
            }
        }
        context.data.save()
    }

    @Description("List ignored entities.")
    fun list(context: Context) {
        context.send().embed("Ignored Entities") {
            field("Users") {
                buildString {
                    context.data.ignored.users.let {
                        if (it.isEmpty()) {
                            append("None of the users are ignored.")
                        }

                        it.mapNotNull(context.guild::getMemberById)
                                .map(IMentionable::getAsMention)
                                .forEach { append("• ").append(it).append('\n') }
                    }
                }
            }
            field("Channel") {
                buildString {
                    context.data.ignored.channels.let {
                        if (it.isEmpty()) {
                            append("None of the channels are ignored.")
                        }

                        it.mapNotNull(context.guild::getTextChannelById)
                                .map(IMentionable::getAsMention)
                                .forEach { append("• ").append(it).append('\n') }
                    }
                }
            }
            field("Roles") {
                buildString {
                    context.data.ignored.roles.let {
                        if (it.isEmpty()) {
                            append("None of the roles are ignored.")
                        }

                        it.mapNotNull(context.guild::getRoleById)
                                .map(IMentionable::getAsMention)
                                .forEach { append("• ").append(it).append('\n') }
                    }
                }
            }
        }.action().queue()
    }
}