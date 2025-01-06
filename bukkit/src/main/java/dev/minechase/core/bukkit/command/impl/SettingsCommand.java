package dev.minechase.core.bukkit.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.minechase.core.bukkit.menu.server.ServersMenu;
import dev.minechase.core.bukkit.menu.settings.SettingsMenu;
import org.bukkit.entity.Player;

@CommandAlias("settings|setting")
public class SettingsCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        new SettingsMenu().openMenu(sender);
    }

}
