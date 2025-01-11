package dev.minechase.core.bukkit.command.context;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.command.CommonCommandContext;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.prefix.model.Prefix;
import dev.minechase.core.api.prefix.model.Prefix;

import java.util.Collection;

public class PrefixContext extends CommonCommandContext<Prefix> {

    public PrefixContext() {
        super("prefixes", Prefix.class);
    }

    @Override
    public Prefix getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String source = context.popFirstArg();
        Prefix prefix = CoreAPI.getInstance().getPrefixHandler().getPrefix(source);

        if (prefix != null) return prefix;

        throw new InvalidCommandArgument(CC.translate("<blend:&4;&c>No prefix with the name '" + source + "' exists.</>"));
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return CoreAPI.getInstance().getPrefixHandler().getPrefixes().values().stream().map(Prefix::getName).toList();
    }

}
