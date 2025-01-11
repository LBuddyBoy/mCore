package dev.minechase.core.velocity.command.context;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.VelocityCommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.motd.model.MOTDTimer;
import net.md_5.bungee.api.ChatColor;

import java.util.Optional;

public class MOTDTimerParam implements ContextResolver<MOTDTimer, VelocityCommandExecutionContext> {

    @Override
    public MOTDTimer getContext(VelocityCommandExecutionContext context) throws InvalidCommandArgument {
        String source = context.popFirstArg();
        Optional<MOTDTimer> motdTimerOpt = CoreVelocity.getInstance().getMotdHandler().getMOTDTimer(source);

        if (motdTimerOpt.isPresent()) {
            return motdTimerOpt.get();
        }

        throw new InvalidCommandArgument(ChatColor.translateAlternateColorCodes('&', "&cThat motd timer does not exist."));

    }
}