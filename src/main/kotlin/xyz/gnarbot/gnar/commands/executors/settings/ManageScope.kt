package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.api.entities.ISnowflake
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.commands.template.annotations.Description
import xyz.gnarbot.gnar.db.guilds.suboptions.CommandOptions

@Description("The scope of the command")
enum class ManageScope {
    USER {
        override fun all(context: Context): Set<String> {
            return context.guild.memberCache.mapTo(HashSet()) { it.user.id }
        }
        override fun transform(map: CommandOptions): MutableSet<String> {
            return map.disabledUsers
        }
        override fun rawTransform(map: CommandOptions): MutableSet<String>? {
            return map.rawDisabledUsers()
        }
    },
    ROLE {
        override fun all(context: Context): Set<String> {
            return context.guild.roleCache.mapTo(HashSet(), ISnowflake::getId)
        }
        override fun transform(map: CommandOptions): MutableSet<String> {
            return map.disabledRoles
        }
        override fun rawTransform(map: CommandOptions): MutableSet<String>? {
            return map.rawDisabledRoles()
        }
    },
    CHANNEL {
        override fun all(context: Context): Set<String> {
            return context.guild.textChannelCache.mapTo(HashSet(), ISnowflake::getId)
        }
        override fun transform(map: CommandOptions): MutableSet<String> {
            return map.disabledChannels
        }
        override fun rawTransform(map: CommandOptions): MutableSet<String>? {
            return map.rawDisabledChannels()
        }
    };

    abstract fun all(context: Context): Set<String>
    abstract fun transform(map: CommandOptions): MutableSet<String>
    abstract fun rawTransform(map: CommandOptions): MutableSet<String>?
}