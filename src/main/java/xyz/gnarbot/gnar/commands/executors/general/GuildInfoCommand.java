package xyz.gnarbot.gnar.commands.executors.general;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.commands.BotInfo;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.Context;
import xyz.gnarbot.gnar.utils.Utils;

import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

@Command(
        aliases = {"guild", "server"},
        description = "Get information this guild."
)
@BotInfo(
        id = 16
)
public class GuildInfoCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        Guild guild = context.getGuild();

        StringJoiner roleStr = new StringJoiner(", ");
        for (Role role : guild.getRoleCache()) {
            roleStr.add(role.getName());
        }

        context.send().embed("Guild Information")
                .setThumbnail(context.getGuild().getIconUrl())
                .field("Name", true, guild.getName())
                .field("Region", true, guild.getRegion().getName())

                .field("ID", true, guild.getId())
                .field("Creation Time", true, guild.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME))

                .field("Owner", true, guild.getOwner() == null ? "No Owner" : guild.getOwner().getAsMention())
                .field("Members", true, guild.getMemberCache().size())

                .field("Text Channels", true, guild.getTextChannelCache().size())
                .field("Voice Channels", true, guild.getVoiceChannelCache().size())

                .field("Verification Level", true, guild.getVerificationLevel())
                .field("Emotes", true, guild.getEmoteCache().size())

                .field("Roles", true, StringUtils.truncate(roleStr.toString(), MessageEmbed.VALUE_MAX_LENGTH))
                .field("Premium", true, context.getData().isPremium()
                        ? "Premium status expires in `" + Utils.getTime(context.getData().remainingPremium()) + "`."
                        : "This guild does not have the premium status.\nVisit our __**[Patreon](https://www.patreon.com/gnarbot)**__ to find out more."
                ).action().queue();
    }
}