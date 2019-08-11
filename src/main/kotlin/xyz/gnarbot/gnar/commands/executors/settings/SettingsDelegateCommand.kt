package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.api.Permission
import xyz.gnarbot.gnar.commands.*

@Command(
        aliases = ["settings", "setting", "set", "config", "configuration", "configure", "opts", "options"],
        description = "Modify bot settings."
)
@BotInfo(
        id = 58,
        category = Category.SETTINGS,
        toggleable = false,
        permissions = [Permission.MANAGE_SERVER]
)
class SettingsDelegateCommand : CommandExecutor() {
    val map = mapOf(
            "autorole" to AutoRoleCommand(),
            "selfroles" to SelfRoleCommand(),
            "ignore" to IgnoreCommand(),
            "commands" to ManageCommandsCommand(),
            "autodelete" to AutoDeleteCommand(),
            "music" to MusicSettingsCommand(),
            "prefix" to PrefixCommand()
    )

    override fun execute(context: Context, label: String, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().embed("Settings Commands") {
                desc { "Here are the available settings you can modify for the context.bot.\n" }
                field("Settings") {
                    buildString {
                        map.forEach { (name, cmd) ->
                            append('`')
                            append(name)
                            append("` • ")
                            append(cmd.info.description).append('\n')
                        }
                    }
                }
            }.action().queue()
            return
        }

        when(args[0]) {
            "reset" -> {
                context.data.reset()
                context.data.save()

                context.send().embed("Settings") {
                    desc { "The guild options for this guild have been reset." }
                }.action().queue()
            }
            else -> {
                val cmd = map[args[0].toLowerCase()]

                if (cmd == null) {
                    context.send().error("Invalid option name. Available options: `${map.keys}`")
                    return
                }

                cmd.execute(context, args[0], args.copyOfRange(1, args.size))
            }
        }
    }
}