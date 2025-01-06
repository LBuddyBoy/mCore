package dev.minechase.core.bukkit.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.minechase.core.bukkit.menu.server.ServersMenu;
import org.bukkit.entity.Player;

@CommandAlias("servers|queues")
@CommandPermission("core.command.servers")
public class ServersCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        new ServersMenu().openMenu(sender);
    }

}
