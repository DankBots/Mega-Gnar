package com.jagrosh.jdautilities.menu

import com.jagrosh.jdautilities.waiter.EventWaiter
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import java.awt.Color
import java.util.concurrent.TimeUnit

abstract class Menu(val waiter: EventWaiter,
                    val user: User?,
                    val title: String?,
                    val description: String?,
                    val color: Color?,
                    val fields: List<MessageEmbed.Field>,
                    val timeout: Long,
                    val unit: TimeUnit,
                    val finally: (Message?) -> Unit)