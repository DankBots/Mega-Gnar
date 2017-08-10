package xyz.gnarbot.gnar.utils;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.db.Database;

public class RiotUtils {
    private static final RethinkDB r = RethinkDB.r;
    private static Connection conn = r.connection().hostname("localhost").port(28015).connect();

    public static String idToChampion(int championID) {
        //Make a new DB named "riot" and a table called champs. Avarel has the JSON file.
        String championName = r.db("riot").table("champs").get(String.valueOf(championID)).getField("name").run(conn);
        return championName;
    }


    public static int championToID(String championName) {
        int id = r.db("riot").table("champs").get(championName).getField("id").run(conn);
        return id;

    }


}
