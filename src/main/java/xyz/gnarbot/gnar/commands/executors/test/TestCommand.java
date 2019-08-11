package xyz.gnarbot.gnar.commands.executors.test;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import xyz.gnarbot.gnar.commands.BotInfo;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.Context;
import xyz.gnarbot.gnar.commands.template.CommandTemplate;
import xyz.gnarbot.gnar.commands.template.annotations.Description;

@Command(
        aliases = "wow"
)
@BotInfo(
        id = 32,
        admin = true,
        category = Category.NONE,
        permissions = Permission.ADMINISTRATOR
)
public class TestCommand extends CommandTemplate {
    @Description("kode is a meme")
    public void test(Context context) {
        System.out.println("meme");
    }

    @Description("natan and his fucking asm <3")
    public void tasty_memes(Context context) {
        System.out.println("meme 2");
    }

    @Description("adrian is bae")
    public void tasty_food(Context context) {
        System.out.println("meme 3");
    }

    @Description("so is avarel")
    public void you_are_a_meme(Context context, Member user) {
        context.send().info(user.getAsMention()).queue();
    }
}
