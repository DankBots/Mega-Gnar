package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color
import java.time.temporal.TemporalAccessor

@Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")
abstract class EmbedProxy<T: EmbedProxy<T>> : EmbedBuilder() {
    override fun clearFields(): T {
        super.clearFields()
        return this as T
    }

    inline fun desc(value: () -> Any?): T {
        return description(value)
    }

    inline fun description(value: () -> Any?): T {
        description(value().toString())
        return this as T
    }

    fun description(description: CharSequence?): T {
        super.setDescription(description)
        return this as T
    }

    override fun appendDescription(description: CharSequence): T {
        super.appendDescription(description)
        return this as T
    }

    override fun setAuthor(name: String?, url: String?, iconUrl: String?): T {
        super.setAuthor(name, url, iconUrl)
        return this as T
    }

    inline fun timestamp(lazy: () -> TemporalAccessor?) {
        this.setTimestamp(lazy())
    }

    override fun setTimestamp(temporal: TemporalAccessor?): T {
        super.setTimestamp(temporal)
        return this as T
    }

    inline fun color(lazy: () -> Color?) {
        this.setColor(lazy())
    }

    override fun setColor(color: Color?): T {
        super.setColor(color)
        return this as T
    }

    inline fun footer(lazy: () -> String?) {
        this.setFooter(lazy(), null)
    }

    override fun setFooter(text: String?, iconUrl: String?): T {
        super.setFooter(text, iconUrl)
        return this as T
    }

    inline fun title(lazy: () -> String?) {
        this.setTitle(lazy())
    }

    override fun setTitle(title: String?): T {
        super.setTitle(title)
        return this as T
    }

    override fun setTitle(title: String?, url: String?): T {
        super.setTitle(title, url)
        return this as T
    }

    fun thumbnail(lazy: () -> String?) {
        this.setThumbnail(lazy())
    }

    override fun setThumbnail(url: String?): T {
        super.setThumbnail(url)
        return this as T
    }

    inline fun image(lazy: () -> String?) {
        this.setImage(lazy())
    }

    override fun setImage(url: String?): T {
        super.setImage(url)
        return this as T
    }

    override fun addField(field: MessageEmbed.Field?): T {
        super.addField(field)
        return this as T
    }

    override fun addField(name: String?, value: String?, inline: Boolean): T {
        super.addField(name, value, inline)
        return this as T
    }

    override fun addBlankField(inline: Boolean): T {
        super.addBlankField(inline)
        return this as T
    }

    inline fun field(inline: Boolean = false) = addBlankField(inline)

    inline fun field(name: String?, inline: Boolean = false, value: Any?): T {
        super.addField(name, value.toString(), inline)
        return this as T
    }

    inline fun field(name: String?, inline: Boolean = false, value: () -> Any?): T {
        super.addField(name, value().toString(), inline)
        return this as T
    }
}