package xyz.gnarbot.gnar.commands.template.parser;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import xyz.gnarbot.gnar.BotLoader;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.template.annotations.Description;
import xyz.gnarbot.gnar.commands.template.annotations.Name;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parsers {
    public static final Parser<String> STRING = new Parser<>("string", "Plain text", (c, s) -> s);

    public static final Parser<Integer> INTEGER = new Parser<>("integer", "Integer number", (c, s) -> {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    });
    public static final Parser<Double> DECIMAL = new Parser<>("number", "Number, can be decimal", (c, s) -> {
        try {
            return Double.valueOf(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    });

    private static final Pattern durationPattern = Pattern.compile("^(?:(?:(\\d+):)?(\\d+):)?(\\d+)$");
    public static final Parser<Duration> DURATION = new Parser<>("hh:mm:ss", "Timestamp", (c, s) -> {
        Matcher m = durationPattern.matcher(s);

        if (!m.find()) {
            return null;
        }

        String[] group = {m.group(1), m.group(2), m.group(3)};
        long hours = group[0] == null ? 0 : Long.parseLong(group[0]);
        long minutes = group[1] == null ? 0 : Long.parseLong(group[1]);
        long seconds = group[2] == null ? 0 : Long.parseLong(group[2]);

        minutes += hours * 60;
        seconds += minutes * 60;

        return Duration.ofSeconds(seconds);
    });

    private static final Pattern memberPattern = Pattern.compile("<@!?(\\d+)>");
    public static final Parser<Member> MEMBER = new Parser<>("@user", "User mention or name", (c, s) -> {
        Matcher matcher = memberPattern.matcher(s);
        if (matcher.find()) {
            return c.getGuild().getMemberById(matcher.group(1));
        } else {
            List<Member> list = c.getGuild().getMembersByName(s, false);
            return list.isEmpty() ? null : list.get(0);
        }
    });

    private static final Pattern textChannelPattern = Pattern.compile("<#(\\d+)>");
    public static final Parser<TextChannel> TEXT_CHANNEL = new Parser<>("#channel", "Channel mention or name", (c, s) -> {
        Matcher matcher = textChannelPattern.matcher(s);
        if (matcher.find()) {
            return c.getGuild().getTextChannelById(matcher.group(1));
        } else {
            List<TextChannel> list = c.getGuild().getTextChannelsByName(s, false);
            return list.isEmpty() ? null : list.get(0);
        }
    });
    public static final Parser<VoiceChannel> VOICE_CHANNEL = new Parser<>("voice channel", "Voice channel name", (c, s) -> {
        List<VoiceChannel> list = c.getGuild().getVoiceChannelsByName(s, false);
        return list.isEmpty() ? null : list.get(0);
    });

    private static final Pattern rolePattern = Pattern.compile("<@&(\\d+)>");
    public static final Parser<Role> ROLE = new Parser<>("@role", "Role mention or name", (c, s) -> {
        Matcher matcher = rolePattern.matcher(s);
        if (matcher.find()) {
            return c.getGuild().getRoleById(matcher.group(1));
        } else {
            List<Role> list = c.getGuild().getRolesByName(s, false);
            return list.isEmpty() ? null : list.get(0);
        }
    });

    public static final Parser<CommandExecutor> COMMAND = new Parser<>("_command", "Command label", (c, s) -> {
        if (s.startsWith(BotLoader.BOT.getConfiguration().getPrefix())) {
            return BotLoader.BOT.getCommandRegistry().getCommand(s.substring(BotLoader.BOT.getConfiguration().getPrefix().length()));
        } else {
            return BotLoader.BOT.getCommandRegistry().getCommand(s);
        }
    });

    public static final Map<Class<?>, Parser<?>> PARSER_MAP = new HashMap<>();
    static {
        PARSER_MAP.put(String.class, Parsers.STRING);
        PARSER_MAP.put(int.class, Parsers.INTEGER);
        PARSER_MAP.put(Integer.class, Parsers.INTEGER);
        PARSER_MAP.put(double.class, Parsers.DECIMAL);
        PARSER_MAP.put(Double.class, Parsers.DECIMAL);
        PARSER_MAP.put(Member.class, Parsers.MEMBER);
        PARSER_MAP.put(Role.class, Parsers.ROLE);
        PARSER_MAP.put(TextChannel.class, Parsers.TEXT_CHANNEL);
        PARSER_MAP.put(VoiceChannel.class, Parsers.VOICE_CHANNEL);
        PARSER_MAP.put(Duration.class, Parsers.DURATION);
        PARSER_MAP.put(CommandExecutor.class, Parsers.COMMAND);
    }

    public static <T extends Enum<T>> Parser<T> createEnumParser(Class<T> cls) {
        Name ann0 = cls.getAnnotation(Name.class);
        Description ann = cls.getAnnotation(Description.class);

        String name = ann0 != null ? ann0.value() : null;
        if (name == null) {
            StringJoiner sj = new StringJoiner(", ");
            for (T item: cls.getEnumConstants()) {
                sj.add(item.name().toLowerCase());
            }
            name = sj.toString();
        }

        String desc = ann != null ? ann.value() : cls.getSimpleName();

        return new Parser<>(name, desc, (c, s) -> {
            try {
                return Enum.valueOf(cls, s.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        });
    }
}
