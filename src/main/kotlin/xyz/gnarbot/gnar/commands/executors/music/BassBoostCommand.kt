package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicManager


@Command(
      aliases = ["bass", "bb"],
      description = "Bass boost the damn out of your music.",
      usage = "high|medium|low"
)

@BotInfo(
     id = 79,
     category = Category.MUSIC,
     scope = Scope.VOICE
)

class BassBoostCommand : MusicCommandExecutor(true, true){
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        val query = args[0]

        if(query.isEmpty()) {
            context.send().embed {
                title { "Bass Boost" }
                field("Bass Boost Presets", false) {
                    "`Off`\n `Low`\n `Medium`\n `High`\n `Insane`"
                }
            }
        }

        when(query) {
            "off" -> manager.disableEqualizer()
            "low" -> manager.bassboost(0.25F, 0.15F)
            "medium" -> manager.bassboost(0.50F, 0.25F)
            "high" -> manager.bassboost(0.75F, 0.50F)
            "insane" -> manager.bassboost(1F, 0.75F)
            else -> {
                context.send().error("$query is not a preset.")
            }
        }
    }
}