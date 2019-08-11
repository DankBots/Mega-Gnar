package xyz.gnarbot.gnar.commands.executors.admin;

import net.dv8tion.jda.api.JDA;
import xyz.gnarbot.gnar.commands.*;

@Command(
        aliases = "shutdown",
        description = "Shutdown the context.getBot()."
)
@BotInfo(
        id = 2,
        admin = true,
        category = Category.NONE
)
public class ShutdownCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        try {
            context.getBot().getPlayers().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (JDA jda : context.getBot().getShardManager().getShardCache()) {
            jda.shutdown();
        }

        System.exit(21);
    }
}
