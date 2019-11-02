package xyz.gnarbot.gnar.commands.executors.media;

import org.json.JSONObject;
import org.w3c.dom.Document;
import xyz.gnarbot.gnar.commands.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
            URL url;
            if (args.length >= 1 && args[0] != null) {
                switch (args[0]) {
                    case "png":
                    case "jpg":
                    case "gif":
                        url = new URL("https://api.thecatapi.com/v1/images/search?mime_types=" + args[0] + "&api_key=" + apiKey);
                        break;
                    default:
                        context.send().error("Not a valid picture type. `[png, jpg, gif]`").queue();
                        return;
                }
            } else {
                url = new URL("https://api.thecatapi.com/v1/images/search?api_key=" + apiKey);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = br.readLine();
            String jsonCompat = line.substring(1, line.length()-1);
            System.out.println(jsonCompat);
            JSONObject jso = new JSONObject(jsonCompat);
            context.send().embed()
                    .setImage(jso.getString("url"))
                    .action().queue();
        } catch (Exception e) {
            context.send().error("Unable to find cats to sooth the darkness of your soul.").queue();
            e.printStackTrace();
        }
    }
}
