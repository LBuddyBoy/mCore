package dev.minechase.core.bukkit.command;

import co.aikar.commands.PaperCommandManager;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.command.context.*;
import dev.minechase.core.bukkit.command.impl.*;
import dev.minechase.core.bukkit.command.impl.punishment.impl.MuteCommand;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;

@Getter
public class CommandHandler implements IModule {

    private PaperCommandManager commandManager;

    @Override
    public void load() {
        this.commandManager = new PaperCommandManager(CorePlugin.getInstance());

        Arrays.asList(
                new RankContext(),
                new MultiScopeContext(),
                new TimeDurationContext(),
                new CoreServerContext(),
                new CorePlayerContext()
        ).forEach(context -> context.register(this.commandManager));

        this.commandManager.getCommandCompletions().registerCompletion("rankPermissions", new RankContext.RankPermissionCompletion());

        this.commandManager.registerCommand(new AltsCommand());
        this.commandManager.registerCommand(new RankCommand());
        this.commandManager.registerCommand(new GrantsCommand());
        this.commandManager.registerCommand(new UserCommand());
        this.commandManager.registerCommand(new QueueCommand());
        this.commandManager.registerCommand(new LogsCommand());
        this.commandManager.registerCommand(new HubCommand());
        this.commandManager.registerCommand(new SettingsCommand());
        this.commandManager.registerCommand(new ServersCommand());
        this.commandManager.registerCommand(new PunishmentsCommand());
        this.commandManager.registerCommand(new MuteCommand());
    }

    @Override
    public void unload() {

    }

}
