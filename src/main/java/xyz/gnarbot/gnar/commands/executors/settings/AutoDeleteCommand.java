package xyz.gnarbot.gnar.commands.executors.settings;

import net.dv8tion.jda.api.Permission;
import xyz.gnarbot.gnar.commands.*;

@Command(
        aliases = {"autodelete", "autodel"}
)
@BotInfo(
        id = 81,
        scope = Scope.GUILD,
        permissions = Permission.MANAGE_SERVER,
        category = Category.SETTINGS
)
public class AutoDeleteCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        String message;
        if (context.getData().getCommand().isAutoDelete()) {
            context.getData().getCommand().setAutoDelete(false);
            context.getData().save();

            message = "The bot will no longer automatically delete messages after 10 seconds.";
        } else {
            context.getData().getCommand().setAutoDelete(true);
            context.getData().save();

            message = "The bot will now delete messages after 10 seconds.";
        }
        context.send().info(message).queue();
    }
}
