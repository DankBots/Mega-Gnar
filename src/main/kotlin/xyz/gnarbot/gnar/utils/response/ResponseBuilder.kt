package xyz.gnarbot.gnar.utils.response

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.requests.RestAction
import org.apache.commons.lang3.exception.ExceptionUtils
import xyz.gnarbot.gnar.utils.EmbedProxy
import xyz.gnarbot.gnar.utils.Utils
import java.awt.Color
import javax.annotation.CheckReturnValue

open class ResponseBuilder(private val channel: MessageChannel) {
    /**
     * Quick-reply to a message.
     *
     * @param text The text to send.
     * @return The Message created by this function.
     */
    open fun text(text: String): RestAction<Message> {
        return channel.sendMessage(text)
    }

    /**
     * Send a standard info message.
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    open fun info(msg: String): RestAction<Message> {
        return embed {
            desc  { msg }
        }.action()
    }

    /**
     * Send a standard error message.
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    open fun error(msg: String): RestAction<Message> {
        return embed {
            title { "Error" }
            desc  { msg }
            color { Color.RED }
        }.action()
    }

    /**
     * Send a standard exception message.
     *
     * @return The Message created by this function.
     */
    open fun exception(exception: Exception): RestAction<Message> {
        if(exception.toString().contains("decoding")) {
            return embed {
                title { "Exception" }
                desc {
                    buildString {
                        append("```\n")
                        append(exception.toString())
                        append("```\n")
                        val link = Utils.hasteBin(ExceptionUtils.getStackTrace(exception))
                        if (link != null) {
                            append("[Full stack trace.](").append("Probably a music error tbh").append(')')
                        } else {
                            append("HasteBin down.")
                        }
                    }
                }
                color { Color.RED }
            }.action()
        }
        return embed {
            title { "Exception" }
            desc {
                buildString {
                    append("For some reason this track causes errors, ignore this.")
                }
            }
            color { Color.RED }
        }.action()
    }

    /**
     * Creates an EmbedBuilder to be used to creates an embed to send.
     */
    open fun embed(): ResponseEmbedBuilder = embed(null)

    /**
     * Creates an EmbedBuilder to be used to creates an embed to send.
     *
     * @param title Title of the embed.
     */
    open fun embed(title: String?): ResponseEmbedBuilder = ResponseEmbedBuilder().apply {
        title { title }
    }

    /**
     * Creates an EmbedBuilder to be used to creates an embed to send.
     * This builder can use [ResponseEmbedBuilder.action] to quickly send the built embed.
     *
     * @param title Title of the embed.
     */
    inline fun embed(title: String? = null, value: ResponseEmbedBuilder.() -> Unit): ResponseEmbedBuilder {
        return embed(title).apply(value)
    }

    inner class ResponseEmbedBuilder : EmbedProxy<ResponseEmbedBuilder>() {
        @CheckReturnValue
        fun action(): RestAction<Message> {
            return channel.sendMessage(build())
        }
    }
}