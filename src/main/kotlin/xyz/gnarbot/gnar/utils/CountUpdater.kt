package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.sharding.ShardManager
import okhttp3.*
import org.json.JSONObject
import xyz.gnarbot.gnar.Bot
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class CountUpdater(private val bot: Bot, shardManager: ShardManager) {
    val client: OkHttpClient = OkHttpClient.Builder().build()
    val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    init {
        shardManager.shardCache.forEachIndexed { index, jda ->
            executor.scheduleAtFixedRate({ update(jda) }, 10L + index * 5, 30, TimeUnit.MINUTES)
        }
    }

    fun shutdown() {
        executor.shutdown()
    }

    private fun update(jda: JDA) {
        if (jda.status != JDA.Status.CONNECTED) return

        Bot.LOG.info("Sending shard updates for shard ${jda.shardInfo.shardId}")
        updateCarbonitex(jda)
        updateAbal(jda)
        updateDiscordBots(jda)
    }

    private fun createCallback(name: String, jda: JDA): Callback {
        return object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Bot.LOG.error("$name update failed for shard ${jda.shardInfo.shardId}: ${e.message}")
                call.cancel()
            }

            override fun onResponse(call: Call, response: Response) {
                response.close()
            }
        }
    }

    private fun updateDiscordBots(jda: JDA) {
        val auth = bot.credentials.discordBots ?: return

        val json =  JSONObject()
                .put("server_count", jda.guildCache.size())
                .put("shard_id", jda.shardInfo.shardId)
                .put("shard_count", bot.credentials.totalShards)

        val request = Request.Builder()
                .url("https://discordbots.org/api/bots/201503408652419073/stats")
                .header("User-Agent", "Gnar Bot")
                .header("Authorization", auth)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(RequestBody.create(HttpUtils.JSON, json.toString()))
                .build()

        client.newCall(request).enqueue(createCallback("discordbots.org", jda))
    }

    private fun updateAbal(jda: JDA) {
        val auth = bot.credentials.abal ?: return

        val json =  JSONObject()
                .put("server_count", jda.guildCache.size())
                .put("shard_id", jda.shardInfo.shardId)
                .put("shard_count", bot.credentials.totalShards)

        val request = Request.Builder()
                .url("https://bots.discord.pw/api/bots/201503408652419073/stats")
                .header("User-Agent", "Gnar Bot")
                .header("Authorization", auth)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(RequestBody.create(HttpUtils.JSON, json.toString()))
                .build()

        client.newCall(request).enqueue(createCallback("bots.discord.pw", jda))
    }

    private fun updateCarbonitex(jda: JDA) {
        val authCarbon = bot.credentials.carbonitex ?: return

        val json = JSONObject()
                .put("key", authCarbon)
                .put("shardid", jda.shardInfo.shardId)
                .put("shardcount", bot.credentials.totalShards)
                .put("servercount", jda.guildCache.size())

        val request = Request.Builder()
                .url("https://www.carbonitex.net/discord/data/botdata.php")
                .header("User-Agent", "Gnar Bot")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(RequestBody.create(HttpUtils.JSON, json.toString()))
                .build()

        client.newCall(request).enqueue(createCallback("carbonitex.net", jda))
    }
}