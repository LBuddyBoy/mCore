package dev.minechase.core.velocity.command;

import co.aikar.commands.VelocityCommandManager;
import dev.minechase.core.velocity.command.*;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.command.context.MOTDTimerParam;
import dev.minechase.core.velocity.command.context.RankParam;
import dev.minechase.core.velocity.command.context.TimeParam;
import dev.minechase.core.velocity.command.context.UUIDParam;
import dev.minechase.core.velocity.command.impl.LockdownCommand;
import dev.minechase.core.velocity.command.impl.MOTDTimerCommand;
import dev.minechase.core.velocity.motd.model.MOTDTimer;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CommandHandler implements IModule {

    private VelocityCommandManager commandManager;

    @Override
    public void load() {
        this.commandManager.getCommandCompletions().registerCompletion("rank", c -> CoreVelocity.getInstance().getRankHandler().getRanks().values().stream().map(Rank::getName).collect(Collectors.toList()));
        this.commandManager.getCommandCompletions().registerCompletion("durations", s -> Arrays.asList("7d", "14d", "30d", "perm"));

        this.commandManager.getCommandCompletions().registerCompletion(
                "context-example",
                context -> Arrays.asList(
                        "%timer-display% &fwill be ending in &a%timer-time-left%",
                        "%timer-time-left%",
                        "%timer-time-left-short% (1d 1 h 2m 3s)",
                        "%timer-time-left-shorter% (No seconds)",
                        "%timer-time-left-hhmmss%"
                )
        );

        this.commandManager.getCommandCompletions().registerCompletion(
                "motd-timers",
                context -> CoreVelocity.getInstance().getMotdHandler().getMotdTimers().keySet()
        );

        this.commandManager.getCommandContexts().registerContext(MOTDTimer.class, new MOTDTimerParam());
        this.commandManager.getCommandContexts().registerContext(TimeDuration.class, new TimeParam());
        this.commandManager.getCommandContexts().registerContext(UUID.class, new UUIDParam());
        this.commandManager.getCommandContexts().registerContext(Rank.class, new RankParam());

        this.commandManager.registerCommand(new LockdownCommand());
        this.commandManager.registerCommand(new MOTDTimerCommand());
    }

    @Override
    public void unload() {

    }
}
