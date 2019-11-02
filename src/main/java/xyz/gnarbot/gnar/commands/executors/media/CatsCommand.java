package xyz.gnarbot.gnar.commands.executors.media;

import org.w3c.dom.Document;
import xyz.gnarbot.gnar.commands.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;

@Command(
        aliases = {"cats", "cat"},
        description = "Grab random cats for you."
)
@BotInfo(
        id = 24,
        category = Category.MEDIA
)
public class CatsCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        try {
            String apiKey = context.getBot().getCredentials().getCat();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc;

            if (args.length >= 1 && args[0] != null) {
                switch (args[0]) {
                    case "png":
                    case "jpg":
                    case "gif":
                        doc = db.parse("https://api.thecatapi.com/v1/images/search?mime_types=" + args[0] + "&api_key=" + apiKey);
                        break;
                    default:
                        context.send().error("Not a valid picture type. `[png, jpg, gif]`").queue();
                        return;
                }
            } else {
                doc = db.parse(new URL("https://api.thecatapi.com/v1/images/search?api_key=" + apiKey)
                        .openStream());
            }
            String url = doc.getElementsByTagName("url").item(0).getTextContent();
            context.send().embed()
                    .setImage(url)
                    .action().queue();
        } catch (Exception e) {
            context.send().error("Unable to find cats to sooth the darkness of your soul.").queue();
            e.printStackTrace();
        }
    }
}
