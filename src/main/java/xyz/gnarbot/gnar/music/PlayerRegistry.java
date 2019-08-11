package xyz.gnarbot.gnar.music;

import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.gnarbot.gnar.Bot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerRegistry {
    private final Logger LOG = LoggerFactory.getLogger("PlayerRegistry");

    private final Map<Long, MusicManager> registry;

    private final Bot bot;
    private final ScheduledExecutorService executor;

    public PlayerRegistry(Bot bot, ScheduledExecutorService executor) {
        this.bot = bot;
        this.executor = executor;

        registry = new ConcurrentHashMap<>(bot.getConfiguration().getMusicLimit());
        executor.scheduleAtFixedRate(() -> clear(false), 20, 10, TimeUnit.MINUTES);
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    @Nonnull
    public MusicManager get(Guild guild) throws MusicLimitException {
        MusicManager manager = registry.get(guild.getIdLong());

        if (manager == null) {
            if (size() >= bot.getConfiguration().getMusicLimit() && !bot.getOptions().ofGuild(guild).isPremium()) {
                throw new MusicLimitException();
            }

            manager = new MusicManager(bot, guild, this);
            registry.put(guild.getIdLong(), manager);
        }

        return manager;
    }

    @Nullable
    public MusicManager getExisting(long id) {
        return registry.get(id);
    }

    @Nullable
    public MusicManager getExisting(Guild guild) {
        return getExisting(guild.getIdLong());
    }

    public void destroy(long id) {
        MusicManager manager = registry.get(id);
        if (manager != null) {
            manager.destroy();
            registry.remove(id);
        }
    }

    public void destroy(Guild guild) {
        destroy(guild.getIdLong());
    }

    public boolean contains(long id) {
        return registry.containsKey(id);
    }

    public boolean contains(Guild guild) {
        return registry.containsKey(guild.getIdLong());
    }

    public void shutdown() {
        clear(true);
    }

    public void clear(boolean force) {
        LOG.info("Cleaning up players (forceful: " + force + ")");
        Iterator<Map.Entry<Long, MusicManager>> iterator = registry.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, MusicManager> entry = iterator.next();
            try {
                if (entry.getValue() == null) {
                    iterator.remove();
                    LOG.warn("Null manager for id " + entry.getKey());
                } else if (force
                        || !entry.getValue().getGuild().getSelfMember().getVoiceState().inVoiceChannel()
                        || entry.getValue().getPlayer().getPlayingTrack() == null) {
                    entry.getValue().destroy();
                    iterator.remove();
                }
            } catch (Exception e) {
                LOG.warn("Exception occured while trying to clean up id " + entry.getKey(), e);
            }
        }

        LOG.info("Finished cleaning up players.");
    }

    public Map<Long, MusicManager> getRegistry() {
        return registry;
    }

    public int size() {
        return registry.size();
    }
}
