package dev.minechase.core.bukkit.command.impl.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.entity.Player;

public class CraftCommand extends BaseCommand {

    @CommandAlias("craft|workbench|wb")
    @CommandPermission("core.command.craft")
    public void gamemode(Player sender) {
        sender.openWorkbench(sender.getLocation(), true);
    }

}
