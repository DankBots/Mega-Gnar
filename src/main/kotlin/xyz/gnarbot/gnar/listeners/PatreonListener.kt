package xyz.gnarbot.gnar.listeners

import com.patreon.resources.Pledge
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.PrivateChannel
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.db.PatreonEntry
import xyz.gnarbot.gnar.db.PremiumKey
import xyz.gnarbot.gnar.utils.Utils
import xyz.gnarbot.gnar.utils.response.respond
import java.awt.Color
import java.io.IOException
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class PatreonListener(private val bot: Bot) : ListenerAdapter() {
    override fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        if (event.author.isBot) return

        val parts = Utils.stringSplit(event.message.contentRaw)

        if (parts.isEmpty()) {
            return
        }

        when (parts[0]) {
            "_patreon", "_patron" -> {
                val user = event.author
                val name = parts.copyOfRange(1, parts.size).joinToString(" ")

                event.channel.respond().info("Looking you up from the Patron list...").queue {
                    GlobalScope.launch {
                        val pledges = try {
                            getPledges()
                        } catch (e: IOException) {
                            it.delete().queue()
                            it.privateChannel.respond().exception(e).queue()
                            return@launch
                        }
                        val pledge = pledges.find {
                            user.id == it.patron.discordId || name.equals(it.patron.fullName, true)
                        }

                        it.delete().queue()

                        if (pledge != null) {
                            processPledge(event.channel, pledge)
                        } else {
                            event.channel.respond().error("**Sorry! I don't recognize you as a patron.**\n" +
                                    "You can support Gnar's development on our __**[Patreon](https://www.patreon.com/gnarbot)**__.\n" +
                                    "If you are a patron, make sure you either link your Discord account or enter your Patreon name, ie. `_patron Bill Gates`").queue()
                        }
                    }
                }
            }
            else -> {
                event.message.privateChannel.respond().error("Invalid command. If you're a patron, use `_patreon` or `_patron.`").queue()
            }
        }
    }

    private fun processPledge(channel: PrivateChannel, pledge: Pledge) {
        val entry = bot.db().getPatreonEntry(pledge.patron.id)
        if (entry != null) {
            val claimedDate = OffsetDateTime.ofInstant(Instant.ofEpochMilli(entry.timeOfClaim), ZoneId.systemDefault())
            sendKeys(channel, "You have already claimed your keys on `${claimedDate.format(DateTimeFormatter.RFC_1123_DATE_TIME)}`. \uD83D\uDC9F", entry.keys)
        } else {
            val keys = Array(pledge.reward.amountCents / 100) { createKey() }.toList()
            sendKeys(channel, "Here are your brand new premium keys. \u2764", keys)
            PatreonEntry(pledge.patron.id, System.currentTimeMillis(), keys).save()
        }
    }

    private fun sendKeys(channel: PrivateChannel, message: String, keys: List<String>) {
        channel.respond().embed("Premium Keys") {
            desc { "$message\nRedeem them in your server using `_redeem (key)`." }
            color { Color.ORANGE }
            field("Keys") {
                buildString {
                    keys.forEach {
                        append("• `")
                        append(it)
                        append("`\n")
                    }
                }
            }
        }.action().queue()
    }

    private fun createKey(): String {
        val key = PremiumKey(UUID.randomUUID().toString(), PremiumKey.Type.PREMIUM, Duration.ofDays(365).toMillis())
        key.save()
        return key.id
    }

    private fun getPledges(): List<Pledge> {
        val campaign = bot.patreon.fetchCampaigns().get().first()

        val pledges = mutableListOf<Pledge>()

        var cursor: String? = null
        do {
            val document = bot.patreon.fetchPageOfPledges(campaign.id, 100, cursor)
            pledges += document.get()
            cursor = bot.patreon.getNextCursorFromDocument(document)
        } while (cursor != null)

        return pledges
    }
}
