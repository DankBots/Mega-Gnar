package xyz.gnarbot.gnar.commands.executors.media;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import xyz.gnarbot.gnar.commands.*;

import java.util.Random;

@Command(
        aliases = {"c&h", "cah"},
        description = "Get Cyanide and Happiness comics.",
        usage = "~id"
)
@BotInfo(
        id = 25,
        category = Category.MEDIA
)
public class ExplosmCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        try {
            Document document;

            int min = 1500;
            int max = 5391;

            String rand;

            if (args.length >= 1) {
                int input;
                try {
                    input = Integer.valueOf(args[0]);

                    if (input > max || input < 100) {
                        context.send().error("Explosm does not have a comic for that number.").queue();
                    }

                    rand = String.valueOf(input);
                } catch (NumberFormatException e) {
                    if (args[0].equalsIgnoreCase("latest")) {
                        rand = "latest";
                    } else {
                        context.send().error("You didn't enter a proper ID number.").queue();
                        return;
                    }
                }
            } else {
                rand = String.valueOf(min + new Random().nextInt(max - min));
            }

            document = Jsoup.connect("http://explosm.net/comics/" + rand + "/").get();

            String url = document.getElementById("main-comic").absUrl("src");

            String logo = "http://explosm.net/img/logo.png";

            context.send().embed("Cyanide and Happiness")
                    .description("No: **" + rand + "**\n")
                    .setThumbnail(logo)
                    .setImage(url)
                    .action().queue();

        } catch (Exception e) {
            context.send().error("Unable to grab Cyanide and Happiness comic, please try again").queue();
            e.printStackTrace();
        }
    }
}
