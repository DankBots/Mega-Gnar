package xyz.gnarbot.gnar.listeners

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.utils.response.ResponseBuilder

class VoiceListener(private val bot: Bot) : ListenerAdapter() {
    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        if (bot.isLoaded) {
            // I just joined the channel
            if (event.member.user == event.jda.selfUser) {
                return
            }

            val guild = event.guild

            bot.players.getExisting(guild.idLong)?.let {
                if (!it.guild.selfMember.voiceState!!.inVoiceChannel()) {
                    bot.players.destroy(guild.idLong)
                } else if (it.isAlone()) {
                    it.queueLeave()
                } else {
                    it.cancelLeave()
                }
            }
        }
    }

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        if (bot.isLoaded) {
            // If the bot left the channel, destroy player.
            if (event.member.user == event.jda.selfUser) {
                bot.players.destroy(event.guild.idLong)
                return
            }

            val guild = event.guild

            bot.players.getExisting(guild.idLong)?.let {
                if (!it.guild.selfMember.voiceState!!.inVoiceChannel()) {
                    bot.players.destroy(guild.idLong)
                } else if (it.isAlone()) {
                    it.queueLeave()
                }
            }
        }
    }

    override fun onGuildVoiceMove(event: GuildVoiceMoveEvent) {
        if (bot.isLoaded) {
            if (event.member.user == event.jda.selfUser) {
                if (!event.guild.selfMember.voiceState!!.inVoiceChannel()) {
                    bot.players.destroy(event.guild.idLong)
                    return
                }

                if (event.channelJoined.id == event.guild.afkChannel?.id) {
                    bot.players.destroy(event.guild.idLong)
                    return
                }

                bot.players.getExisting(event.guild.idLong)?.let {
                    val options = bot.options.ofGuild(event.guild)
                    if (options.music.channels.isNotEmpty()) {
                        if (event.channelJoined.id !in options.music.channels) {
                            it.currentRequestChannel?.let { requestChannel ->
                                ResponseBuilder(requestChannel).error(
                                    "Can not join `${event.channelJoined.name}`, it isn't one of the designated music channels."
                                ).queue()
                            }

                            bot.players.destroy(event.guild.idLong)
                            return
                        }
                    }

                    it.moveAudioConnection(event.channelJoined)

                    if (it.isAlone()) {
                        it.queueLeave()
                    } else {
                        it.cancelLeave()
                    }
                }
                return
            }

            val guild = event.guild

            bot.players.getExisting(guild.idLong)?.let {
                if (!event.guild.selfMember.voiceState!!.inVoiceChannel()) {
                    bot.players.destroy(event.guild.idLong)
                    return
                } else if (it.isAlone()) {
                    it.queueLeave()
                } else {
                    it.cancelLeave()
                }
            }
        }
    }
}