package xyz.gnarbot.gnar.commands.executors.general;

import net.dv8tion.jda.api.entities.Emote;
import xyz.gnarbot.gnar.commands.BotInfo;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.Context;

import java.util.Arrays;

@Command(
        aliases = "react",
        usage = "(message id) (emoji...)",
        description = "Make GNAR react to something, against it's " + "will. You evil prick."
)
@BotInfo(
        id = 20
)
public class ReactCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        if (args.length < 2) {
            context.getBot().getCommandDispatcher().sendHelp(context, getInfo());
            return;
        }

        context.getMessage().getChannel().retrieveMessageById(args[0]).queue(msg -> {
            if (context.getMessage().getEmotes().size() > 0) {
                for (Emote em : context.getMessage().getEmotes()) {
                    msg.addReaction(em).queue();
                }
            } else {
                String[] reactions = Arrays.copyOfRange(args, 1, args.length);

                if (reactions.length == 0) {
                    context.send().error("No reactions detected, robot.").queue();
                    return;
                }

                for (String r : reactions) {
                    msg.addReaction(r).queue();
                }
            }
        }, t -> context.send().error("Invalid message ID `" + args[0] + "`.").queue());
    }
}