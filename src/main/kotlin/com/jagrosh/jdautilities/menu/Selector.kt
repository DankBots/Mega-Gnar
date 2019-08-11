package com.jagrosh.jdautilities.menu

import com.jagrosh.jdautilities.waiter.EventWaiter
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import xyz.gnarbot.gnar.utils.embed
import java.awt.Color
import java.util.concurrent.TimeUnit

class Selector(waiter: EventWaiter,
               user: User?,
               title: String?,
               description: String?,
               color: Color?,
               fields: List<MessageEmbed.Field>,
               val type: Type,
               val options: List<Entry>,
               timeout: Long,
               unit: TimeUnit,
               finally: (Message?) -> Unit) : Menu(waiter, user, title, description, color, fields, timeout, unit, finally) {
    enum class Type {
        REACTIONS,
        MESSAGE
    }

    val cancel = "\u274C"

    var message: Message? = null

    fun display(channel: TextChannel) {
        if (!channel.guild.selfMember.hasPermission(channel, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EMBED_LINKS)) {
            channel.sendMessage(embed("Error") {
                color { Color.RED }
                desc {
                    buildString {
                        append("The bot requires the permission `${Permission.MESSAGE_ADD_REACTION.getName()}`, ")
                        append("`${Permission.MESSAGE_MANAGE.getName()}` and ")
                        append("`${Permission.MESSAGE_EMBED_LINKS.getName()}` for selection menus.")
                    }
                }
            }.build()).queue()
            finally(message)
            return
        }

        channel.sendMessage(embed(title) {
            color { channel.guild.selfMember.color }
            description {
                buildString {
                    append(description).append('\n').append('\n')
                    options.forEachIndexed { index, (name) ->
                        append("${'\u0030' + index}\u20E3 $name\n")
                    }
                }
            }

            field("Select an Option") {
                when (type) {
                    Type.REACTIONS -> "Pick a reaction corresponding to the options."
                    Type.MESSAGE -> "Type a number corresponding to the options. ie: `0` or `cancel`"
                }
            }

            super.fields.forEach {
                addField(it)
            }

            setFooter("This selection will time out in $timeout ${unit.toString().toLowerCase()}.", null)
        }.build()).queue {
            message = it
            when (type) {
                Type.REACTIONS -> {
                    options.forEachIndexed { index, _ ->
                        it.addReaction("${'\u0030' + index}\u20E3").queue()
                    }
                    it.addReaction(cancel).queue()
                }
                Type.MESSAGE -> { /* pass */ }
            }
        }

        when(type) {
            Type.REACTIONS -> {
                waiter.waitFor(MessageReactionAddEvent::class.java) {
                    if (it.reaction.reactionEmote.name == cancel) {
                        finally(message)
                        return@waitFor
                    }

                    val value = it.reaction.reactionEmote.name[0] - '\u0030'
                    it.channel.retrieveMessageById(it.messageIdLong).queue {
                        options[value].action(it)
                    }
                    finally(message)
                }.predicate {
                    when {
                        it.messageIdLong != message?.idLong -> false
                        it.user.isBot -> false
                        user != null && it.user != user -> {
                            it.reaction.removeReaction(it.user).queue()
                            false
                        }
                        else -> {
                            if (it.reaction.reactionEmote.name == cancel) {
                                true
                            } else {
                                val value = it.reaction.reactionEmote.name[0] - '\u0030'
                                if (value in 0 until options.size) {
                                    true
                                } else {
                                    it.reaction.removeReaction(it.user).queue()
                                    false
                                }
                            }
                        }
                    }
                }.timeout(timeout, unit) {
                    finally(message)
                }
            }
            Type.MESSAGE -> {
                waiter.waitFor(GuildMessageReceivedEvent::class.java) {
                    val content = it.message.contentDisplay
                    if (content == "cancel") {
                        finally(message)
                        return@waitFor
                    }

                    val value = content.toIntOrNull() ?: return@waitFor
                    it.channel.retrieveMessageById(it.messageIdLong).queue {
                        options[value].action(it)
                    }
                    finally(message)
                }.predicate {
                    when {
                        it.author.isBot -> false
                        user != null && it.author != user -> {
                            false
                        }
                        else -> {
                            val content = it.message.contentDisplay
                            if (content == "cancel") {
                                true
                            } else {
                                val value = content.toIntOrNull() ?: return@predicate false
                                value in 0 until options.size
                            }
                        }
                    }
                }.timeout(timeout, unit) {
                    finally(message)
                }
            }
        }
    }

    data class Entry(val name: String, val action: (Message) -> Unit)
}