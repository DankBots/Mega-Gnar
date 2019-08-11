package xyz.gnarbot.gnar.commands.executors.fun;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.commands.*;

@Command(
        aliases = "tts",
        usage = "(words...)",
        description = "Text to speech fun."
)
@BotInfo(
        id = 11,
        category = Category.FUN,
        scope = Scope.TEXT,
        permissions = Permission.MESSAGE_TTS
)
public class TextToSpeechCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        if (args.length == 0) {
            context.getBot().getCommandDispatcher().sendHelp(context, getInfo());
            return;
        }

        MessageBuilder builder = new MessageBuilder();
        builder.setTTS(true);
        builder.append(StringUtils.join(args, " "));

        context.getMessage().getChannel().sendMessage(builder.build()).queue();
    }
}
