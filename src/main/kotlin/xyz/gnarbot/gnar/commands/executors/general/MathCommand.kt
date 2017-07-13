package xyz.gnarbot.gnar.commands.executors.general

import net.dv8tion.jda.core.entities.MessageEmbed
import xyz.avarel.aje.Expression
import xyz.avarel.aje.exceptions.AJEException
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.code
import java.awt.Color
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

@Command(
        id = 46,
        aliases = arrayOf("aje"),
        usage = "(expression)",
        description = "User-eval.",
        cooldown = 3000
)
class MathCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Please provide a math expression.").queue()
            return
        }

        context.send().embed("Math") {
            val script = if (args.size == 1) {
                args[0]
            } else {
                args.joinToString(" ")
            }

            val exp = Expression(script)

            try {
                val expr = exp.compile()

                val ast: String = buildString {
                    expr.ast(this, "", true)
                }

                val result = expr.compute()

                field("AST") {
                    code {
                        if (ast.length > MessageEmbed.VALUE_MAX_LENGTH / 2) {
                            "AST can not be displayed."
                        } else {
                            ast
                        }
                    }
                }

                field("Result") {
                    code {
                        result.toString()
                    }
                }
            } catch (e : AJEException) {
                field("Error") {
                    e.message
                }
                color { Color.RED }
            } catch (e : ExecutionException) {
                field("Error") {
                    e.cause?.message ?: e.message
                }
                color { Color.RED }
            } catch (e : TimeoutException) {
                field("Error") {
                    "Script took too long to execute."
                }
                color { Color.RED }
            }
        }.action().queue()
    }
}