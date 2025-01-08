package dev.minechase.core.bukkit.command.context;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.command.CommonCommandContext;
import dev.minechase.core.api.punishment.model.PunishmentType;

import java.util.Arrays;
import java.util.Collection;

public class PunishmentTypeContext extends CommonCommandContext<PunishmentType> {

    public PunishmentTypeContext() {
        super("punishmentTypes", PunishmentType.class);
    }

    @Override
    public PunishmentType getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();

        try {
            return PunishmentType.valueOf(source.toUpperCase());
        } catch (Exception ignored) {

        }

        throw new InvalidCommandArgument(CC.translate("<blend:&4;&c>No punishment type with the name '" + source + "' exists.</>"));
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return Arrays.stream(PunishmentType.values()).map(Enum::name).toList();
    }
}