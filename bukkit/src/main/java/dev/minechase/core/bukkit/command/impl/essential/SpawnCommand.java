package dev.minechase.core.bukkit.command.impl.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import dev.lbuddyboy.commons.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpawnCommand extends BaseCommand {

    @CommandAlias("spawn")
    @CommandPermission("core.command.spawn")
    public void spawn(Player sender) {
        sender.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation());
    }

    @CommandAlias("setspawn")
    @CommandPermission("core.command.setspawn")
    public void setspawn(Player sender) {
        sender.getWorld().setSpawnLocation(sender.getLocation().clone());
        sender.getWorld().save();
        sender.sendMessage(CC.translate("&6Spawnpoint has been updated!"));
    }

}
