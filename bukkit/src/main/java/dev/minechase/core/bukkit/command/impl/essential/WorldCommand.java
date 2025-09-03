package dev.minechase.core.bukkit.command.impl.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldCommand extends BaseCommand {

    @CommandAlias("world")
    @CommandPermission("core.command.world")
    @CommandCompletion("@worlds")
    public void world(Player sender, @Name("world") World world) {
        sender.teleport(world.getSpawnLocation());
        sender.sendMessage(CC.translate("&6Teleporting to &f" + world.getName() + "&6...</>"));
    }

}
