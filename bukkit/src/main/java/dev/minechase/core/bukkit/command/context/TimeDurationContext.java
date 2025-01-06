package dev.minechase.core.bukkit.command.context;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.command.CommonCommandContext;

import java.util.Arrays;
import java.util.Collection;

public class TimeDurationContext extends CommonCommandContext<TimeDuration> {

    public TimeDurationContext() {
        super("durations", TimeDuration.class);
    }

    @Override
    public TimeDuration getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();

        if (source == null || new TimeDuration(source).transform() <= -1) {
            return new TimeDuration("perm");
        } else {
            TimeDuration duration = new TimeDuration(source);

            if (duration.transform() >= 0) {
                return duration;
            }
        }

        throw new InvalidCommandArgument(CC.translate("&cInvalid time inputted."));
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return Arrays.asList("7d", "14d", "30d", "perm");
    }
}