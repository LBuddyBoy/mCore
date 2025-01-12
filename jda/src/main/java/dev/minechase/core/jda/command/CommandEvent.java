package dev.minechase.core.jda.command;

import dev.minechase.core.jda.CoreBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandEvent extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        for (Command command : CoreBot.getInstance().getCommandHandler().getCommands()) {
            if (command.getCmd().equalsIgnoreCase(event.getName())) {
                command.send(event);
            }
        }
    }

}
