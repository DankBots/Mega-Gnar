package xyz.gnarbot.gnar

import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import okhttp3.Request
import org.json.JSONObject
import org.json.JSONTokener
import xyz.gnarbot.gnar.utils.HttpUtils
import xyz.gnarbot.gnar.utils.get
import java.io.File

class Credentials(file: File) {
    val loader: ConfigurationLoader<CommentedConfigurationNode> = HoconConfigurationLoader.builder()
            .setPath(file.toPath()).build()

    val config: CommentedConfigurationNode = loader.load()

    val token: String = config["token"].string.takeIf { !it.isNullOrBlank() } ?: error("Bot token can't be null or blank.")

    val shards = config["shards"].int.takeIf { it != 0 } ?: let {
        val request = Request.Builder().url("https://discordapp.com/api/gateway/bot")
                .header("Authorization", "Bot $token")
                .header("Content-Type", "application/json")
                .build()

        HttpUtils.CLIENT.newCall(request).execute().use { response ->
            response.body()?.let {
                val jso = JSONObject(JSONTokener(it.byteStream()))
                response.close()
                jso.getInt("shards")
            } ?: error("Invalid shards response from Discord")
        }
    }

    val consoleWebhook: String? = config["console webhook"].string

    val abal: String? = config["server counts", "abal"].string
    val carbonitex: String? = config["server counts", "carbonitex"].string
    val discordBots: String? = config["server counts", "discordbots"].string

    val cat: String? = config["cat"].string
    val imgFlip: String? = config["imgflip"].string
    val mashape: String? = config["mashape"].string

    val malUsername: String? = config["mal credentials", "username"].string
    val malPassword: String? = config["mal credentials", "password"].string

    val riotToken: String? = config["riot", "apikey"].string


    val discordFM: String? = config["discordfm"].string
}
