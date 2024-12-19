package dev.minechase.core.bukkit.command;

import co.aikar.commands.PaperCommandManager;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.command.context.CorePlayerContext;
import dev.minechase.core.bukkit.command.impl.UserCommand;
import lombok.Getter;

@Getter
public class CommandHandler implements IModule {

    private PaperCommandManager commandManager;

    @Override
    public void load() {
        this.commandManager = new PaperCommandManager(CorePlugin.getInstance());

        new CorePlayerContext().register(this.commandManager);

        this.commandManager.registerCommand(new UserCommand());
    }

    @Override
    public void unload() {

    }

}
