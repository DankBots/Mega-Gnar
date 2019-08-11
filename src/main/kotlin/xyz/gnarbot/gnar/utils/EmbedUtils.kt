@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("EmbedUtils")
package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.api.EmbedBuilder

@JvmOverloads
fun embed(title: String? = null): EmbedMaker = EmbedMaker().apply {
    title { title }
}

inline fun embed(title: String? = null, value: EmbedMaker.() -> Unit): EmbedMaker {
    return embed(title).apply(value)
}

inline fun EmbedBuilder.field(inline: Boolean = false): EmbedBuilder = addBlankField(inline)

inline fun EmbedBuilder.field(name: String?, inline: Boolean = false, value: Any?): EmbedBuilder {
    addField(name, value.toString(), inline)
    return this
}

inline fun EmbedBuilder.field(name: String?, inline: Boolean = false, value: () -> Any?): EmbedBuilder {
    addField(name, value().toString(), inline)
    return this
}

inline fun EmbedBuilder.desc(value: () -> Any?): EmbedBuilder {
    this.description(value)
    return this
}

inline fun EmbedBuilder.description(value: () -> Any?): EmbedBuilder {
    setDescription(value().toString())
    return this
}