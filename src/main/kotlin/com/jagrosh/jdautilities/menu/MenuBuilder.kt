@file:Suppress("NOTHING_TO_INLINE")

package com.jagrosh.jdautilities.menu

import com.jagrosh.jdautilities.waiter.EventWaiter
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import java.awt.Color
import java.util.concurrent.TimeUnit

@Suppress("UNCHECKED_CAST")
abstract class MenuBuilder<T: MenuBuilder<T>>(val waiter: EventWaiter) {
    companion object {
        @JvmStatic val DEFAULT_FINALLY: (Message?) -> Unit = { it?.delete()?.queue() }
    }

    protected var user: User? = null
    protected var title: String? = "Menu"
    protected var description: String? = null
    protected var color: Color? = null
    protected var finally: (Message?) -> Unit = DEFAULT_FINALLY
    protected var timeout: Long = 20
    protected var unit: TimeUnit = TimeUnit.SECONDS
    protected val fields: MutableList<MessageEmbed.Field> = arrayListOf()

    inline fun title(lazy: () -> String): T {
        return setTitle(lazy())
    }

    fun setTitle(title: String?): T {
        this.title = title
        return this as T
    }

    inline fun desc(lazy: () -> String): T {
        return description(lazy)
    }

    inline fun description(lazy: () -> String): T {
        return setDescription(lazy())
    }

    fun setDescription(description: String?): T {
        this.description = description
        return this as T
    }

    inline fun color(lazy: () -> Color?): T {
        return setColor(lazy())
    }

    fun setColor(color: Color?): T {
        this.color = color
        return this as T
    }

    fun setUser(user: User): T {
        this.user = user
        return this as T
    }

    fun finally(action: (Message?) -> Unit) {
        this.finally = action
    }

    fun setTimeout(timeout: Long, unit: TimeUnit): T {
        this.timeout = timeout
        this.unit = unit
        return this as T
    }

    fun addField(field: MessageEmbed.Field?): T {
        return if (field == null) this as T else addField(field.name, field.value, field.isInline)
    }

    fun addField(name: String?, value: String?, inline: Boolean): T {
        if (name == null && value == null) {
            return this as T
        }
        fields.add(MessageEmbed.Field(name, value, inline))
        return this as T
    }

    fun addBlankField(inline: Boolean): T {
        fields.add(MessageEmbed.Field(EmbedBuilder.ZERO_WIDTH_SPACE, EmbedBuilder.ZERO_WIDTH_SPACE, inline))
        return this as T
    }

    inline fun field(inline: Boolean = false) = addBlankField(inline)

    inline fun field(name: String?, inline: Boolean = false, value: Any?): T {
        addField(name, value.toString(), inline)
        return this as T
    }

    inline fun field(name: String?, inline: Boolean = false, value: () -> Any?): T {
        addField(name, value().toString(), inline)
        return this as T
    }

    abstract fun build(): Any
}