package gg.octave.bot.db

import com.rethinkdb.RethinkDB
import com.rethinkdb.gen.ast.ReqlExpr
import com.rethinkdb.gen.exc.ReqlDriverError
import com.rethinkdb.net.Connection
import com.rethinkdb.net.Cursor
import com.rethinkdb.RethinkDB.r
import com.rethinkdb.ast.ReqlAst
import gg.octave.bot.Launcher
import gg.octave.bot.db.guilds.GuildData
import gg.octave.bot.db.guilds.UserData
import gg.octave.bot.db.premium.PremiumGuild
import gg.octave.bot.db.premium.PremiumUser
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.slf4j.LoggerFactory
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.Protocol
import kotlin.system.exitProcess

class Database(private val name: String) {
    val conn: Connection
    val jedisPool: JedisPool
    val redisson: RedissonClient

    val isOpen: Boolean
        get() = conn.isOpen

    init {
        val creds = Launcher.credentials

        // Init Rethink
        val builder = r.connection()
            .hostname(creds.rethinkHost)
            .port(creds.rethinkPort)

        val rethinkUser = creds.rethinkUsername
        val rethinkAuth = creds.rethinkAuth

        if (!rethinkAuth.isNullOrEmpty()) {
            if (!rethinkUser.isNullOrEmpty()) {
                builder.user(creds.rethinkUsername, rethinkAuth)
            } else {
                builder.authKey(rethinkAuth)
            }
        }

        try {
            conn = builder.connect()
        } catch (e: ReqlDriverError) {
            log.error("Failed to connect to Rethink. Exiting...")
            exitProcess(1)
        }

        if (!r.dbList().run<List<String>>(conn).contains(name)) {
            r.dbCreate(name).run<Any>(conn)
        }

        log.info("Connected to Rethink.")
        conn.use(name)

        // Init Redis
        val redisHost = creds.redisHost
        val redisPort = creds.redisPort
        val redisAuth = creds.redisAuth

        val redisConfig = Config()
        val ssc = redisConfig.useSingleServer()
        ssc.address = "redis://$redisHost:$redisPort"

        if (!redisAuth.isNullOrEmpty()) {
            jedisPool = JedisPool(JedisPoolConfig(), redisHost, redisPort, Protocol.DEFAULT_TIMEOUT, redisAuth)
            ssc.password = redisAuth
        } else {
            jedisPool = JedisPool(JedisPoolConfig(), redisHost, redisPort, 5000)
        }

        redisson = Redisson.create(redisConfig)
    }

    fun getGuildData(id: String) = get("guilds_v2", id, GuildData::class.java)
    fun getPremiumKey(id: String) = get("keys", id, PremiumKey::class.java)
    fun getUserData(id: String) = get("users", id, UserData::class.java)
    fun getPatreonEntry(id: String) = get("patreon", id, PatreonEntry::class.java)
    fun hasPremiumUser(id: String) = isOpen && r.table("premiumusers")[id].coerceTo("bool").run(conn)

    fun getPremiumUser(id: String): PremiumUser = query(PremiumUser::class.java) {
        table("premiumusers")[id].default_(r.hashMap("id", id).with("pledgeAmount", "0.0"))
    }!!
    fun getPremiumUsers() = query<Cursor<PremiumUser>, PremiumUser>(PremiumUser::class.java) { table("premiumusers") }?.toList() ?: emptyList()

    fun getPremiumGuild(id: String) = get("premiumguilds", id, PremiumGuild::class.java)
    fun getPremiumGuilds(redeemer: String) = query<Cursor<PremiumGuild>, PremiumGuild>(PremiumGuild::class.java) {
        table("premiumguilds").filter { it.g("redeemer").eq(redeemer) }
    }

    fun close() = conn.close()

    operator fun <T> get(table: String, id: String, cls: Class<T>): T? = if (isOpen) r.table(table)[id].run(conn, cls) else null
    fun <T, P> query(cls: Class<P>, q: RethinkDB.() -> ReqlAst): T? = if (!isOpen) null else r.q().run<T, P>(conn, cls)

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}
