package xyz.gnarbot.gnar;

import javax.security.auth.login.LoginException;
import java.io.File;

/**
 *
 * Main bot class.
 *
 * @author Avarel, Xevryll
 */
public final class BotLoader {
    public static Bot BOT;

    public static void main(String[] args) throws LoginException, InterruptedException {
        BOT = new Bot(new Credentials(new File("credentials.conf")), () -> new Configuration(new File("bot.conf")));
    }
//    public static final Logger LOG = LoggerFactory.getLogger("Bot");
//
//    public static final Credentials KEYS = new Credentials(new File("credentials.conf"));
//    public static Configuration CONFIG = new Configuration(new File("bot.conf"));
//
//    private static final EventWaiter waiter = new EventWaiter();
//
//    private static final Database database = new Database("bot");
//    private static final OptionsRegistry optionsRegistry = new OptionsRegistry();
//    private static final PlayerRegistry playerRegistry = new PlayerRegistry(Executors.newSingleThreadScheduledExecutor());
//
//    private static final MyAnimeListAPI malAPI = new MyAnimeListAPI(KEYS.getMalUsername(), KEYS.getMalPassword());
//
//    private static final CommandRegistry commandRegistry = new CommandRegistry();
//    private static final CommandDispatcher commandDispatcher = new CommandDispatcher(commandRegistry, Executors.newWorkStealingPool());
//
//    private static ShardManager shardManager;
//    private static CountUpdater countUpdater;
//
//    public static LoadState STATE = LoadState.LOADING;
//
//    public static void main(String[] args) throws InterruptedException, LoginException {
//        String token = KEYS.getWebHookToken();
//        if (token != null) {
//            DiscordLogBack.enable(new WebhookClientBuilder(KEYS.getWebHookID(), token).build());
//        }
//
//        LOG.info("Initializing the Discord bot.");
//
//        LOG.info("Name  :\t" + CONFIG.getName());
//        LOG.info("Shards:\t" + KEYS.getTotalShards());
//        LOG.info("Prefix:\t" + CONFIG.getPrefix());
//        LOG.info("Admins:\t" + CONFIG.getAdmins());
//        LOG.info("JDA v.:\t" + JDAInfo.VERSION);
//
//        BotListener botListener = new BotListener();
//        VoiceListener voiceListener = new VoiceListener();
//        shardManager = new DefaultShardManagerBuilder()
//                .setToken(Bot.KEYS.getToken())
//                .setMaxReconnectDelay(32)
//                .setShardsTotal(Bot.KEYS.getTotalShards())
//                .setShards(Bot.KEYS.getShardStart(), Bot.KEYS.getShardEnd())
//                .setAudioSendFactory(new NativeAudioSendFactory())
//                .addEventListeners(waiter, botListener, voiceListener)
//                .setGameProvider(i -> Game.playing(String.format(Bot.CONFIG.getGame(), i)))
//                .setBulkDeleteSplittingEnabled(false)
//                .build();
//
//        countUpdater = new CountUpdater(shardManager);
//
//        STATE = LoadState.COMPLETE;
//
//        LOG.info("The bot is now fully connected to Discord.");
//    }
//
//    public static void reloadConfig() {
//        CONFIG = new Configuration(new File("bot.conf"));
//    }
//
//    public static MyAnimeListAPI getMALAPI() {
//        return malAPI;
//    }
//
//    public static Database db() {
//        return database;
//    }
//
//    public static CommandRegistry getCommandRegistry() {
//        return commandRegistry;
//    }
//
//    public static CommandDispatcher getCommandDispatcher() {
//        return commandDispatcher;
//    }
//
//    public static PlayerRegistry getPlayers() {
//        return playerRegistry;
//    }
//
//    public static OptionsRegistry getOptions() {
//        return optionsRegistry;
//    }
//
//    public static EventWaiter getWaiter() {
//        return waiter;
//    }
//
//    public static ShardManager getShardManager() {
//        return shardManager;
//    }
//
//    public static CountUpdater getCountUpdater() {
//        return countUpdater;
//    }
//
//    public static Guild getGuildById(long id) {
//        return getShard(MiscUtil.getShardForGuild(id, KEYS.getTotalShards())).getGuildById(id);
//    }
//
//    public static long getUserCount() {
//        return shardManager.getUserCache().size();
//    }
//
//    public static JDA getShard(int id) {
//        return shardManager.getShardById(id);
//    }
//
//    public static void restart() throws InterruptedException {
//        LOG.info("Restarting the Discord bot shards.");
//        shardManager.restart();
//        LOG.info("Discord bot shards have now restarted.");
//    }
}
