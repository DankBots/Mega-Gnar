package com.jagrosh.jdautilities.menu

import com.jagrosh.jdautilities.waiter.EventWaiter
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.requests.RestAction
import xyz.gnarbot.gnar.utils.embed
import java.awt.Color
import java.util.concurrent.TimeUnit

class Paginator(waiter: EventWaiter,
                user: User?,
                title: String?,
                description: String?,
                color: Color?,
                fields: List<MessageEmbed.Field>,
                val emptyMessage: String?,
                val list: List<List<String>>,
                timeout: Long,
                unit: TimeUnit,
                finally: (Message?) -> Unit) : Menu(waiter, user, title, description, color, fields, timeout, unit, finally) {
    val LEFT = "\u25C0"
    val STOP = "\u23F9"
    val RIGHT = "\u25B6"

    fun display(channel: TextChannel) {
        if (!channel.guild.selfMember.hasPermission(channel, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EMBED_LINKS)) {
            channel.sendMessage(embed("Error") {
                color { Color.RED }
                desc {
                    buildString {
                        append("The bot requires the permission `${Permission.MESSAGE_ADD_REACTION.getName()}` and ")
                        append("`${Permission.MESSAGE_EMBED_LINKS.getName()}` for pagination menus.")
                    }
                }
            }.build()).queue()
            finally(null)
            return
        }

        paginate(channel, 1)
    }

    fun display(message: Message) {
        if (!message.textChannel.guild.selfMember.hasPermission(message.textChannel, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EMBED_LINKS)) {
            message.channel.sendMessage(embed("Error") {
                color { Color.RED }
                description {
                    buildString {
                        append("The bot requires the permission `${Permission.MESSAGE_ADD_REACTION.getName()}`, ")
                        append("`${Permission.MESSAGE_MANAGE.getName()}` and ")
                        append("`${Permission.MESSAGE_EMBED_LINKS.getName()}` for pagination menus.")
                    }
                }
            }.build()).queue()
            finally(null)
            return
        }

        paginate(message, 1)
    }

    fun paginate(channel: TextChannel, page: Int) {
        if (list.isEmpty()) {
            channel.sendMessage(renderEmptyPage()).queue()
            return
        }

        val pageNum = page.coerceIn(1, list.size)
        val msg = renderPage(page)
        initialize(channel.sendMessage(msg), pageNum)
    }

    fun paginate(message: Message, page: Int) {
        if (list.isEmpty()) {
            message.editMessage(renderEmptyPage()).queue()
            return
        }

        val pageNum = page.coerceIn(1, list.size)
        val msg = renderPage(page)
        initialize(message.editMessage(msg), pageNum)
    }

    private fun initialize(action: RestAction<Message>, page: Int) {
        action.queue { message ->
            if (list.size > 1) {
                message.addReaction(LEFT).queue()
                message.addReaction(STOP).queue()
                message.addReaction(RIGHT).queue {
                    waiter.waitFor(MessageReactionAddEvent::class.java) {
                        val pageNew = when (it.reactionEmote.name) {
                            LEFT -> page - 1
                            RIGHT -> page + 1
                            STOP -> {
                                finally(message)
                                return@waitFor
                            }
                            else -> {
                                finally(message)
                                error("Internal pagination error")
                            }
                        }

                        it.reaction.removeReaction(it.user).queue()

                        if (pageNew != page) {
                            message?.editMessage(renderPage(pageNew))?.queue {
                                paginate(it, pageNew)
                            }
                        }
                    }.predicate {
                        when {
                            it.messageIdLong != message?.idLong -> false
                            it.user.isBot -> false
                            user != null && it.user != user -> {
                                it.reaction.removeReaction(it.user).queue()
                                false
                            }
                            else -> when (it.reactionEmote.name) {
                                LEFT, STOP, RIGHT -> true
                                else -> false
                            }
                        }
                    }.timeout(timeout, unit) {
                        finally(message)
                    }
                }
            }
        }
    }

    private fun renderPage(page: Int): Message {
        val pageNum = page.coerceIn(1, list.size)

        return MessageBuilder().setEmbed(embed(title) {
            setColor(color)

            val items = list[pageNum - 1]
            desc {
                buildString {
                    description?.let { append(it).append('\n').append('\n') }
                    items.forEachIndexed { index, s ->
                        append('`').append(index + 1 + (pageNum - 1) * list[0].size).append("` ")
                        append(s).append('\n')
                    }
                }
            }

            super.fields.forEach {
                addField(it)
            }

            setFooter("Page $pageNum/${list.size}", null)
        }.build()).build()
    }

    private fun renderEmptyPage(): Message {
        return MessageBuilder().setEmbed(embed(title) {
            setColor(color)

            emptyMessage?.let(this::description)

            super.fields.forEach {
                addField(it)
            }
        }.build()).build()
    }
}