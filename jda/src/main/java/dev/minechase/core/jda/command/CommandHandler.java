package dev.minechase.core.jda.command;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.jda.CoreBot;
import dev.minechase.core.jda.command.impl.SyncCommand;
import dev.minechase.core.jda.command.impl.SyncResetCommand;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandHandler implements IModule {

    private final List<Command> commands;

    public CommandHandler() {
        this.commands = new ArrayList<>();
    }

    private void registerCommands() {
        this.commands.add(new SyncCommand());
        this.commands.add(new SyncResetCommand());

        for (Command command : this.commands) {
            CoreBot.getInstance().getGuild().upsertCommand(Commands.slash(command.getCmd(), command.getDescription())
                    .setDefaultPermissions(command.getDefaultMemberPermissions())
                    .setGuildOnly(true).addOptions(command.getOptionData())).queue();

            System.out.println("[Command Handler] Registered '" + command.getCmd() + "' command");
        }

        System.out.println("[Command Handler] Registered " + this.commands.size() + " commands in total!");
    }

    @Override
    public void load() {
        registerCommands();
    }

    @Override
    public void unload() {

    }
}
