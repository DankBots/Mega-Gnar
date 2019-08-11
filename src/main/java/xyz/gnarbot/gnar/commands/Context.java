package xyz.gnarbot.gnar.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.db.guilds.GuildData;
import xyz.gnarbot.gnar.utils.response.GuildResponseBuilder;
import xyz.gnarbot.gnar.utils.response.ResponseBuilder;

public class Context {
    private final Bot bot;
    private final GuildData guildOptions;
    private final Message message;
    private final TextChannel textChannel;
    private final Guild guild;
    private final JDA jda;
    private final Member member;
    private final User user;

    public Context(Bot bot, GuildMessageReceivedEvent event) {
        this.bot = bot;
        this.jda = event.getJDA();

        this.message = event.getMessage();
        this.user = event.getAuthor();

        this.textChannel = event.getChannel();
        this.guild = event.getGuild();
        this.member = event.getMember();
        this.guildOptions = bot.getOptions().ofGuild(getGuild());
    }

    public Bot getBot() {
        return bot;
    }

    public GuildData getData() {
        return this.guildOptions;
    }

    public Message getMessage() {
        return this.message;
    }

    public TextChannel getTextChannel() {
        return this.textChannel;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public JDA getJDA() {
        return this.jda;
    }

    public Member getMember() {
        return this.member;
    }

    public Member getSelfMember() {
        return this.guild.getSelfMember();
    }

    public VoiceChannel getVoiceChannel() {
        return getMember().getVoiceState().getChannel();
    }

    public User getUser() {
        return this.user;
    }

    public ResponseBuilder send() {
        return new GuildResponseBuilder(getTextChannel());
    }
}
