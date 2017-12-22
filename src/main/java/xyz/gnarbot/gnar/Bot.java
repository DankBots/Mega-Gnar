package xyz.gnarbot.gnar;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.utils.MiscUtil;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.gnarbot.gnar.commands.CommandRegistry;
import xyz.gnarbot.gnar.commands.dispatcher.CommandDispatcher;
import xyz.gnarbot.gnar.db.Database;
import xyz.gnarbot.gnar.db.OptionsRegistry;
import xyz.gnarbot.gnar.listener.VoiceListener;
import xyz.gnarbot.gnar.listeners.BotListener;
import xyz.gnarbot.gnar.music.PlayerRegistry;
import xyz.gnarbot.gnar.utils.CountUpdater;
import xyz.gnarbot.gnar.utils.DiscordFM;
import xyz.gnarbot.gnar.utils.DiscordLogBack;
import xyz.gnarbot.gnar.utils.MyAnimeListAPI;

import javax.security.auth.login.LoginException;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class Bot {
    public static final Logger LOG = LoggerFactory.getLogger("Bot");

    private final Credentials credentials;

    private final Supplier<Configuration> configurationGenerator;
    private final Database database;
    private final OptionsRegistry optionsRegistry;
    private final PlayerRegistry playerRegistry;
    private final MyAnimeListAPI myAnimeListAPI;
    private final RiotApi riotApi;
    private final DiscordFM discordFM;
    private final CommandRegistry commandRegistry;
    private final CommandDispatcher commandDispatcher;
    private final EventWaiter eventWaiter = new EventWaiter();
    private final ShardManager shardManager;
    private final CountUpdater countUpdater;
    private Configuration configuration;
    private boolean loadState;

    public Bot(
            Credentials credentials,
            Supplier<Configuration> configurationGenerator
    ) throws LoginException {
        this.credentials = credentials;
         database = new Database(credentials);

        this.configurationGenerator = configurationGenerator;
        reloadConfiguration();
        optionsRegistry  = new OptionsRegistry(this);
        playerRegistry  = new PlayerRegistry(this, Executors.newSingleThreadScheduledExecutor());
        discordFM = new DiscordFM(this);
        commandRegistry  = new CommandRegistry(this);
        commandDispatcher  = new CommandDispatcher(this, commandRegistry, Executors.newWorkStealingPool());

        LOG.info("Initializing the Discord bot.");

        String token = this.credentials.getWebHookToken();
        if (token != null) {
            LOG.info("Connected to Discord web hook.");
            DiscordLogBack.enable(new WebhookClientBuilder(this.credentials.getWebHookID(), token).build());
        } else {
            LOG.warn("Not connected to Discord web hook.");
        }

        LOG.info("Name  :\t" + configuration.getName());
        LOG.info("Shards:\t" + this.credentials.getTotalShards());
        LOG.info("Prefix:\t" + configuration.getPrefix());
        LOG.info("Admins:\t" + configuration.getAdmins());
        LOG.info("JDA v.:\t" + JDAInfo.VERSION);

        shardManager = new DefaultShardManagerBuilder()
                .setToken(credentials.getToken())
                .setMaxReconnectDelay(32)
                .setShardsTotal(credentials.getTotalShards())
                .setShards(credentials.getShardStart(), credentials.getShardEnd())
                .setAudioSendFactory(new NativeAudioSendFactory())
                .addEventListeners(eventWaiter, new BotListener(this), new VoiceListener(this))
                .setGameProvider(i -> Game.playing(String.format(configuration.getGame(), i)))
                .setBulkDeleteSplittingEnabled(false)
                .build();

        countUpdater = new CountUpdater(this, shardManager);

        loadState = true;

        myAnimeListAPI = new MyAnimeListAPI(credentials.getMalUsername(), credentials.getMalPassword());

        String riotApiKey = credentials.getRiotAPIKey();
        ApiConfig apiConfig = new ApiConfig();
        if (riotApiKey != null) {
            apiConfig.setKey(riotApiKey);
        }
        riotApi = new RiotApi(new ApiConfig());

        LOG.info("The bot is now fully connected to Discord.");
    }

    public void reloadConfiguration() {
        configuration = configurationGenerator.get();
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public CountUpdater getCountUpdater() {
        return countUpdater;
    }

    public Guild getGuildById(long id) {
        return getJDA(MiscUtil.getShardForGuild(id, credentials.getTotalShards())).getGuildById(id);
    }

    public MyAnimeListAPI getMyAnimeListAPI() {
        return myAnimeListAPI;
    }

    public DiscordFM getDiscordFM() {
        return discordFM;
    }

    public Database db() {
        return database;
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public CommandDispatcher getCommandDispatcher() {
        return commandDispatcher;
    }

    public PlayerRegistry getPlayers() {
        return playerRegistry;
    }

    public OptionsRegistry getOptions() {
        return optionsRegistry;
    }

    public EventWaiter getEventWaiter() {
        return eventWaiter;
    }

    public JDA getJDA(int id) {
        return shardManager.getShardById(id);
    }

    public RiotApi getRiotAPI() {
        return riotApi;
    }

    public void restart() throws InterruptedException {
        LOG.info("Restarting the Discord bot shards.");
        shardManager.restart();
        LOG.info("Discord bot shards have now restarted.");
    }

    public boolean isLoaded() {
        return loadState;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Credentials getCredentials() {
        return credentials;
    }
}
