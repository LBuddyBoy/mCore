package dev.minechase.core.velocity.command.context;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.VelocityCommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import net.md_5.bungee.api.ChatColor;

public class TimeParam implements ContextResolver<TimeDuration, VelocityCommandExecutionContext> {

    @Override
    public TimeDuration getContext(VelocityCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();

        if (source == null || new TimeDuration(source).transform() <= -1) {
            return new TimeDuration("perm");
        } else {
            TimeDuration duration = new TimeDuration(source);

            if (duration.transform() >= 0) {
                return duration;
            }
        }

        throw new InvalidCommandArgument(ChatColor.translateAlternateColorCodes('&', "&cInvalid time inputted."));
    }
}