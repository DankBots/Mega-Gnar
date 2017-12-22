package xyz.gnarbot.gnar.db;

import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.net.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.gnarbot.gnar.Credentials;
import xyz.gnarbot.gnar.guilds.GuildData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.rethinkdb.RethinkDB.r;

public class Database {
    private static final Logger LOG = LoggerFactory.getLogger("Database");
    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    private final Connection conn;

    public Database(@Nonnull Credentials credentials) {
        Connection conn = null;
        try {
            Connection.Builder builder = r.connection()
                    .hostname(credentials.getDatabaseAddr())
                    .port(credentials.getDatabasePort());
            // potential spot for authentication
            if (credentials.getDatabaseUser() != null && credentials.getDatabasePass() != null) {
                builder.user(credentials.getDatabaseUser(), credentials.getDatabasePass());
            }
            conn = builder.connect();
            String dbName = credentials.getDatabaseName();
            if (r.dbList().<List<String>>run(conn).contains(dbName)) {
                LOG.info("Connected to database.");
                conn.use(dbName);
            } else {
                LOG.info("Rethink Database `" + dbName + "` is not present. Closing connection.");
                close();
                System.exit(0);
            }
        } catch (ReqlDriverError e) {
            LOG.error("Rethink Database connection failed.", e);
            System.exit(0);
        }
        this.conn = conn;
    }

    public boolean isOpen() {
        return conn != null && conn.isOpen();
    }

    public void close() {
        conn.close();
    }

    @Nullable
    public GuildData getGuildData(String id) {
        return isOpen() ? r.table("guilds_v2").get(id).run(conn, GuildData.class) : null;
    }

    public void saveGuildData(GuildData guildData) {
        if (isOpen()) r.table("guilds_v2").insert(guildData)
                .optArg("conflict", "replace")
                .runNoReply(conn);
    }

    public void deleteGuildData(String id) {
        if (isOpen()) r.table("guilds_v2").get(id)
                .delete()
                .runNoReply(conn);
    }

    @Nullable
    public PremiumKey getPremiumKey(String id) {
        return isOpen() ? r.table("keys").get(id).run(conn, PremiumKey.class) : null;
    }

    public void savePremiumKey(PremiumKey key) {
        if (isOpen()) r.table("keys").insert(key)
                .optArg("conflict", "replace")
                .runNoReply(conn);
    }

    public void deleteKey(String id) {
        if (isOpen()) r.table("keys").get(id)
                .delete()
                .runNoReply(conn);
    }

    public ScheduledExecutorService getExecutor() {
        return exec;
    }
}
