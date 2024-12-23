package dev.minechase.core.bukkit.command.context;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.command.CommonCommandContext;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.ScopedPermission;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class RankContext extends CommonCommandContext<Rank> {

    public RankContext() {
        super("ranks", Rank.class);
    }

    @Override
    public Rank getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String source = context.popFirstArg();
        Rank rank = CoreAPI.getInstance().getRankHandler().getRank(source);

        if (rank != null) return rank;

        throw new InvalidCommandArgument(CC.translate("<blend:&4;&c>No rank with the name '" + source + "' exists.</>"));
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return CoreAPI.getInstance().getRankHandler().getSortedRanks().stream().map(Rank::getName).toList();
    }

    public static class RankPermissionCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

        @Override
        public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
            Rank rank = context.getContextValue(Rank.class);

            return rank.getPermissions().stream().map(ScopedPermission::getPermissionNode).toList();
        }

    }

}
