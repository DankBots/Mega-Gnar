package xyz.gnarbot.gnar.commands;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BotInfo {
    int id();

    /** @return If the command requires the user to be the bot administrator */
    boolean admin() default false;

    /** @return If the command requires the user to be a donator. */
    boolean donor() default false;

    /** @return If the command can be disabled. */
    boolean toggleable() default true;

    /** @return Command cooldown in milliseconds. */
    long cooldown() default 0L;

    /** @return The category of the command. */
    Category category() default Category.GENERAL;

    /** @return The intended location where the command is used.. */
    Scope scope() default Scope.GUILD;

    /** @return The Discord permissions the member requires. */
    Permission[] permissions() default {};

    String roleRequirement() default "";
}
