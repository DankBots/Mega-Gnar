package com.jagrosh.jdautilities.menu

import com.jagrosh.jdautilities.waiter.EventWaiter
import net.dv8tion.jda.api.entities.Message

class SelectorBuilder(waiter: EventWaiter) : MenuBuilder<SelectorBuilder>(waiter) {
    private val options: MutableList<Selector.Entry> = mutableListOf()
    private var type: Selector.Type = Selector.Type.MESSAGE

    fun addOption(option: String, action: (Message) -> Unit): SelectorBuilder {
        options.add(Selector.Entry(option, action))
        return this
    }

    fun setType(type: Selector.Type): SelectorBuilder {
        this.type = type
        return this
    }

    override fun build(): Selector {
        return Selector(waiter, user, title, description, color, fields, type, options, timeout, unit, finally)
    }
}