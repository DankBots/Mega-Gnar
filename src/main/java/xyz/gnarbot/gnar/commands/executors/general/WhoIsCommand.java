package xyz.gnarbot.gnar.commands.executors.general;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.commands.BotInfo;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.Context;
import xyz.gnarbot.gnar.commands.template.parser.Parsers;

import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

@Command(
        aliases = {"user", "whois", "who"},
        usage = "[user]",
        description = "Get information on a user."
)
@BotInfo(
        id = 22
)
public class WhoIsCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        final Member member;

        if (args.length == 0) {
            member = context.getMember();
        } else {
            member = Parsers.MEMBER.parse(context, args[0]);
        }

        if (member == null) {
            context.send().error("You did not mention a valid user.").queue();
            return;
        }

        StringJoiner roleStr = new StringJoiner(", ");
        for (Role role : member.getRoles()) {
            roleStr.add(role.getName());
        }

        context.send().embed("Who is " + member.getEffectiveName() + "?")
                .setColor(member.getColor())
                .setThumbnail(member.getUser().getEffectiveAvatarUrl())

                .field("Name", true, member.getUser().getName())
                .field("Discriminator", true, member.getUser().getDiscriminator())

                .field("ID", true, member.getUser().getId())
                .field("Status", true, StringUtils.capitalize(member.getOnlineStatus().getKey()))

                .field("Creation Time", true, member.getUser().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .field("Join Date", true, member.getTimeJoined().format(DateTimeFormatter.RFC_1123_DATE_TIME))

                .field("Nickname", true, member.getNickname() != null ? member.getNickname() : "No nickname.")
                .field("Activity", true, !member.getActivities().isEmpty() ? member.getActivities().get(0).getName() : "No game.")

                .field("Roles", true, roleStr)

                .action().queue();
    }
}
