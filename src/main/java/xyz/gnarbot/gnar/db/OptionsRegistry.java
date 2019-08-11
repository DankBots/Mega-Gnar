package xyz.gnarbot.gnar.db;

import net.dv8tion.jda.api.entities.Guild;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.db.guilds.GuildData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OptionsRegistry {
    private final Bot bot;

    public OptionsRegistry(Bot bot) {
        this.bot = bot;
    }

    @Nonnull
    public GuildData ofGuild(Guild guild) {
        GuildData data = bot.db().getGuildData(guild.getId());
        return data == null ? new GuildData(guild.getId()) : data;
    }

    @Nullable
    public GuildData ofGuild(long id) {
        Guild guild = bot.getGuildById(id);
        return guild == null ? null : ofGuild(guild);
    }
}
