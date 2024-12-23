package dev.minechase.core.bukkit.command.context;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.commons.util.command.CommonCommandContext;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class CorePlayerContext extends CommonCommandContext<AsyncCorePlayer> {

    public CorePlayerContext() {
        super("players", AsyncCorePlayer.class);
    }

    @Override
    public AsyncCorePlayer getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String source = context.popFirstArg();

        return new AsyncCorePlayer(source);
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }
}
