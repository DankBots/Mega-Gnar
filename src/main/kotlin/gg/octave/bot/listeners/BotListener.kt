package gg.octave.bot.listeners

import gg.octave.bot.Launcher
import gg.octave.bot.db.Database
import gg.octave.bot.db.OptionsRegistry
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.*
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.json.JSONObject
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import java.awt.Color
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit

class BotListener : EventListener {
    private val log = LoggerFactory.getLogger(BotListener::class.java)

    override fun onEvent(event: GenericEvent) {
        when (event) {
            is GuildJoinEvent -> onGuildJoin(event)
            is GuildMessageReceivedEvent -> onGuildMessageReceived(event)
            is GuildLeaveEvent -> onGuildLeave(event)
            is StatusChangeEvent -> onStatusChange(event)
            is ReadyEvent -> onReady(event)
            is ResumedEvent -> onResume(event)
            is ReconnectedEvent -> onReconnect(event)
            is DisconnectEvent -> onDisconnect(event)
            is ExceptionEvent -> onException(event)
        }
    }

    private fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot && event.author === event.jda.selfUser) {
            if (OptionsRegistry.ofGuild(event.guild).command.isAutoDelete) {
                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
            }
        }
    }

    private fun onGuildJoin(event: GuildJoinEvent) {
        //Don't fire this if the SelfMember joined a longish time ago. This avoids discord fuckups.
        if (event.guild.selfMember.timeJoined.isBefore(OffsetDateTime.now().minusSeconds(30))) return

        //Greet message start.
        val embedBuilder = EmbedBuilder()
            .setThumbnail(event.jda.selfUser.effectiveAvatarUrl)
            .setColor(Color.BLUE)
            .setDescription("Welcome to Octave! The highest quality Discord music bot!\n" +
                "Please check the links below to get help, and use `_help` to get started!")
            .addField("Important Links",
                "[Support Server](https://discord.gg/musicbot)\n" +
                    "[Website](https://octave.gg) \n" +
                    "[Invite Link](https://invite.octave.gg)\n" +
                    "[Patreon](https://patreon.com/octave)", true)
            .setFooter("Thanks for using Octave!")


        //Find the first channel we can talk to.
        val channel = event.guild.textChannels.firstOrNull { it.canTalk() }
            ?: return

        channel.sendMessage(embedBuilder.build()).queue { m: Message -> m.delete().queueAfter(1, TimeUnit.MINUTES) }

        Launcher.datadog.gauge("octave_bot.guilds", Launcher.shardManager.guildCache.size())
        Launcher.datadog.gauge("octave_bot.users", Launcher.shardManager.userCache.size())
        Launcher.datadog.gauge("octave_bot.players", Launcher.players.size().toLong())
        Launcher.datadog.incrementCounter("octave_bot.guildJoin")
        postStats(event.jda)
    }

    private fun onGuildLeave(event: GuildLeaveEvent) {
        Launcher.players.destroy(event.guild)
        Launcher.datadog.gauge("octave_bot.guilds", Launcher.shardManager.guildCache.size())
        Launcher.datadog.gauge("octave_bot.users", Launcher.shardManager.userCache.size())
        Launcher.datadog.gauge("octave_bot.players", Launcher.players.size().toLong())
        Launcher.datadog.incrementCounter("octave_bot.guildLeave")
        postStats(event.jda)
    }

    private fun onStatusChange(event: StatusChangeEvent) {
        log.info("Shard #{} Status: {} -> {}", event.jda.shardInfo.shardId, event.oldStatus, event.newStatus)
    }

    private fun onReady(event: ReadyEvent) {
        Launcher.datadog.incrementCounter("octave_bot.shardReady")
        log.info("JDA ${event.jda.shardInfo.shardId} is ready.")
        postStats(event.jda)
    }

    private fun onResume(event: ResumedEvent) {
        Launcher.datadog.incrementCounter("octave_bot.shardResume")
        log.info("JDA ${event.jda.shardInfo.shardId} has resumed.")
    }

    private fun onReconnect(event: ReconnectedEvent) {
        Launcher.datadog.incrementCounter("octave_bot.shardReconnect")
        log.info("JDA ${event.jda.shardInfo.shardId} has reconnected.")
        postStats(event.jda)
    }

    private fun onDisconnect(event: DisconnectEvent) {
        Launcher.datadog.incrementCounter("octave_bot.shardDisconnect")

        if (event.isClosedByServer) {
            log.info("JDA {} disconnected (closed by server). Code: {} {}",
                event.jda.shardInfo.shardId, event.serviceCloseFrame?.closeCode ?: -1, event.closeCode)
        } else {
            log.info("JDA {} disconnected. Code: {} {}",
                event.jda.shardInfo.shardId, event.serviceCloseFrame?.closeCode
                ?: -1, event.clientCloseFrame?.closeReason ?: "")
        }
    }

    private fun onException(event: ExceptionEvent) {
        Launcher.datadog.incrementCounter("octave_bot.exception")
        if (!event.isLogged) log.error("Exception in JDA {}", event.jda.shardInfo.shardId, event.cause)
    }

    private fun postStats(jda: JDA) {
        Launcher.database.jedisPool.resource.use {
            it.hset("stats", jda.shardInfo.shardId.toString(), JSONObject()
                .put("guild_count", jda.guildCache.size())
                .put("cached_users", jda.userCache.size())
                .toString()
            )
        }
    }
}
