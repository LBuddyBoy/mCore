package dev.minechase.core.bukkit.command.context;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.commons.util.command.CommonCommandContext;
import dev.minechase.core.api.api.MultiScope;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

public class MultiScopeContext extends CommonCommandContext<MultiScope> {

    public MultiScopeContext() {
        super("scopes", MultiScope.class);
    }

    @Override
    public MultiScope getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String source = context.popFirstArg();

        return new MultiScope(source);
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return Arrays.asList("Example #1: GLOBAL", "Example #2: skyblock,factions,etc");
    }
}
