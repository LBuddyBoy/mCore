package dev.minechase.core.velocity.command.context;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.VelocityCommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.velocity.CoreVelocity;
import net.md_5.bungee.api.ChatColor;

public class RankParam implements ContextResolver<Rank, VelocityCommandExecutionContext> {

    @Override
    public Rank getContext(VelocityCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();

        Rank rank = CoreVelocity.getInstance().getRankHandler().getRank(source);

        if (rank != null) return rank;

        throw new InvalidCommandArgument(ChatColor.translateAlternateColorCodes('&', "&cThat rank does not exist."));
    }
}