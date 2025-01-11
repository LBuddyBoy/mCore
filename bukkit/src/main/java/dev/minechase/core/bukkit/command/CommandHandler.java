package dev.minechase.core.bukkit.command;

import co.aikar.commands.PaperCommandManager;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.command.context.*;
import dev.minechase.core.bukkit.command.impl.*;
import dev.minechase.core.bukkit.command.impl.admin.*;
import dev.minechase.core.bukkit.command.impl.punishment.PunishmentsCommand;
import dev.minechase.core.bukkit.command.impl.punishment.impl.*;
import dev.minechase.core.bukkit.command.impl.staff.AltsCommand;
import dev.minechase.core.bukkit.command.impl.staff.IPHistoryCommand;
import dev.minechase.core.bukkit.command.impl.staff.StaffCommand;
import dev.minechase.core.bukkit.command.impl.staff.TwoFactorCommand;
import lombok.Getter;

import java.util.Arrays;

@Getter
public class CommandHandler implements IModule {

    private PaperCommandManager commandManager;

    @Override
    public void load() {
        this.commandManager = new PaperCommandManager(CorePlugin.getInstance());

        Arrays.asList(
                new RankContext(),
                new TagContext(),
                new MultiScopeContext(),
                new TimeDurationContext(),
                new UUIDContext(),
                new CoreServerContext(),
                new CorePlayerContext()
        ).forEach(context -> context.register(this.commandManager));

        this.commandManager.getCommandCompletions().registerCompletion("rankPermissions", new RankContext.RankPermissionCompletion());

        this.commandManager.registerCommand(new CoreCommand());
        this.commandManager.registerCommand(new AltsCommand());
        this.commandManager.registerCommand(new RankCommand());
        this.commandManager.registerCommand(new TagCommand());
        this.commandManager.registerCommand(new GrantsCommand());
        this.commandManager.registerCommand(new UserCommand());
        this.commandManager.registerCommand(new QueueCommand());
        this.commandManager.registerCommand(new LogsCommand());
        this.commandManager.registerCommand(new HubCommand());
        this.commandManager.registerCommand(new SettingsCommand());
        this.commandManager.registerCommand(new ServersCommand());
        this.commandManager.registerCommand(new IPHistoryCommand());
        this.commandManager.registerCommand(new AdminCommand());
        this.commandManager.registerCommand(new StaffCommand());
        this.commandManager.registerCommand(new DisguiseCommand());
        this.commandManager.registerCommand(new TwoFactorCommand());

        /*
        Punishments
         */

        this.commandManager.registerCommand(new BanCommand());
        this.commandManager.registerCommand(new BlacklistCommand());
        this.commandManager.registerCommand(new KickCommand());
        this.commandManager.registerCommand(new MuteCommand());
        this.commandManager.registerCommand(new WarnCommand());
        this.commandManager.registerCommand(new PunishmentsCommand());
    }

    @Override
    public void unload() {

    }

}
