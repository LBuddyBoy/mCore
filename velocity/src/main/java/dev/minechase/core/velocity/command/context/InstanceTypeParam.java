package dev.minechase.core.velocity.command.context;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.VelocityCommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.instance.model.InstanceType;
import net.md_5.bungee.api.ChatColor;

public class InstanceTypeParam implements ContextResolver<InstanceType, VelocityCommandExecutionContext> {

    @Override
    public InstanceType getContext(VelocityCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();
        InstanceType type = CoreVelocity.getInstance().getInstanceHandler().getTypes().get(source);

        if (type != null) return type;

        throw new InvalidCommandArgument(ChatColor.translateAlternateColorCodes('&', "&cThat instance type does not exist."));
    }
}