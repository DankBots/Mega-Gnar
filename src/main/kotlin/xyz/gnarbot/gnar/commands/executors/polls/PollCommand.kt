package xyz.gnarbot.gnar.commands.executors.polls

import net.dv8tion.jda.api.EmbedBuilder
import xyz.gnarbot.gnar.commands.BotInfo
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.utils.Utils
import xyz.gnarbot.gnar.utils.desc
import java.time.Duration
import java.util.concurrent.TimeUnit

@Command(
        aliases = ["poll"],
        usage = "(description) | (time) | (option 1);(option 2);...",
        description = "Create a poll."
)
@BotInfo(
        id = 76,
        cooldown = 10000
)
class PollCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        val parts = args.joinToString(" ").split('|').map(String::trim)

        if (parts.size < 3) {
            context.bot.commandDispatcher.sendHelp(context, info)
            return
        }

        val description = parts[0]

        val time = Utils.parseTime(parts[1])

        if (time < Duration.ofSeconds(10).toMillis()) {
            context.send().error("Poll duration can't be less than 10 seconds.").queue()
            return
        } else if (time > Duration.ofHours(1).toMillis()) {
            context.send().error("Poll duration can not be greater than 1 hours.").queue()
            return
        }

        val options = parts[2].split(';').map(String::trim)

        if (options.size <= 1) {
            context.send().error("Please offer more than 1 option for the poll. `_poll Why tho? | 1m | (option 1);(option 2);...`").queue()
            return
        }

        context.send().embed("Poll") {
            desc { description }
            field("Vote through clicking the reactions on the choices below!") {
                buildString {
                    options.forEachIndexed { index, option ->
                        appendln("${'\u0030' + index}\u20E3 **$option**")
                    }
                }
            }
            footer { "Results will be final in ${parts[1]}." }
        }.action().queue {
            for (index in 0 until options.size) {
                it.addReaction("${'\u0030' + index}\u20E3").queue()
            }

            it.editMessage(EmbedBuilder(it.embeds[0]).apply {
                desc { "Voting has ended! Check the results in the newer messages!" }
                clearFields()
            }.build()).queueAfter(time, TimeUnit.MILLISECONDS) {
                context.send().embed("Poll Results") {
                    desc { description }

                    var topVotes = 0
                    val winners = mutableListOf<Int>()

                    field("Voting has ended! Here are the results!") {
                        buildString {
                            it.reactions.forEach { reaction ->
                                val value = reaction.reactionEmote.name[0] - '\u0030'
                                if (value !in 0 until options.size) return@forEach

                                options[value].let {
                                    appendln("${reaction.reactionEmote.name} **$it** — __${reaction.count - 1} Votes__")

                                    if (reaction.count - 1 > topVotes) {
                                        winners.clear()
                                        topVotes = reaction.count - 1
                                        winners += value
                                    } else if (reaction.count - 1 == topVotes) {
                                        winners += value
                                    }
                                }
                            }
                        }
                    }

                    it.clearReactions().queue()

                    field("Winner") {
                        winners.joinToString(prefix = "**", postfix = "**") { options[it] }
                    }
                }.action().queue()
            }
        }
    }
}
