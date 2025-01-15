package dev.minechase.core.bukkit.command;

import co.aikar.commands.PaperCommandManager;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.command.context.*;
import dev.minechase.core.bukkit.command.impl.*;
import dev.minechase.core.bukkit.command.impl.admin.*;
import dev.minechase.core.bukkit.command.impl.essential.*;
import dev.minechase.core.bukkit.command.impl.punishment.PunishmentsCommand;
import dev.minechase.core.bukkit.command.impl.punishment.impl.*;
import dev.minechase.core.bukkit.command.impl.staff.*;
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
                new EnchantmentContext(),
                new GameModeContext(),
                new PrefixContext(),
                new NPCContext(),
                new HologramContext(),
                new TagContext(),
                new MultiScopeContext(),
                new TimeDurationContext(),
                new UUIDContext(),
                new CoreServerContext(),
                new CorePlayerContext()
        ).forEach(context -> context.register(this.commandManager));

        this.commandManager.getCommandCompletions().registerCompletion("rankPermissions", new RankContext.RankPermissionCompletion());
        this.commandManager.getCommandCompletions().registerCompletion("disguiseRanks", new RankContext.DisguiseRanksCompletion());
        this.commandManager.getCommandCompletions().registerCompletion("hologramLines", new HologramContext.HologramLinesCompletion());
        this.commandManager.getCommandCompletions().registerCompletion("npcLines", new NPCContext.NPCLinesCompletion());

        this.commandManager.registerCommand(new NPCCommand());
        this.commandManager.registerCommand(new HologramCommand());

        this.commandManager.registerCommand(new ClearCommand());
        this.commandManager.registerCommand(new CraftCommand());
        this.commandManager.registerCommand(new EnchantCommand());
        this.commandManager.registerCommand(new FeedCommand());
        this.commandManager.registerCommand(new FixCommand());
        this.commandManager.registerCommand(new GameModeCommand());
        this.commandManager.registerCommand(new ListCommand());
        this.commandManager.registerCommand(new RenameCommand());
        this.commandManager.registerCommand(new SpawnCommand());
        this.commandManager.registerCommand(new TeleportCommand());
        this.commandManager.registerCommand(new InvseeCommand());

        this.commandManager.registerCommand(new ReportCommand());
        this.commandManager.registerCommand(new CoreCommand());
        this.commandManager.registerCommand(new AltsCommand());
        this.commandManager.registerCommand(new RebootCommand());
        this.commandManager.registerCommand(new FilterCommand());
        this.commandManager.registerCommand(new RankCommand());
        this.commandManager.registerCommand(new PrefixCommand());
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
        this.commandManager.registerCommand(new TwoFactorCommand());
        this.commandManager.registerCommand(new NoteCommand());
        this.commandManager.registerCommand(new SyncCommand());

        this.commandManager.registerCommand(new DisguiseCommand());
        this.commandManager.registerCommand(new UnDisguiseCommand());

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
