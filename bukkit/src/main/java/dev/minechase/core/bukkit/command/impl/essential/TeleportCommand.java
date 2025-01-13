package dev.minechase.core.bukkit.command.impl.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand extends BaseCommand {

    public static String BACK_METADATA = "CORE_BACK_LOCATION";

    @CommandAlias("teleport|tp")
    @CommandPermission("core.command.teleport")
    @CommandCompletion("@players")
    public void gamemode(Player sender, @Name("player") AsyncCorePlayer corePlayer) {
        Player player = corePlayer.getPlayer();

        if (player == null) {
            sender.sendMessage(CC.translate("<blend:&4;&c>That player is not online.</>"));
            return;
        }

        sender.teleport(player);
        sender.sendMessage(CC.translate("&6Teleporting to &f" + player.getName() + "&6...</>"));
    }

    @CommandAlias("teleporthere|tph|s|tphere")
    @CommandPermission("core.command.teleporthere")
    @CommandCompletion("@players")
    public void teleporthere(Player sender, @Name("player") AsyncCorePlayer corePlayer) {
        Player player = corePlayer.getPlayer();

        if (player == null) {
            sender.sendMessage(CC.translate("<blend:&4;&c>That player is not online.</>"));
            return;
        }

        player.teleport(sender);
        sender.sendMessage(CC.translate("&6Teleporting &f" + player.getName() + " to yourself"));
    }

    @CommandAlias("back")
    @CommandPermission("core.command.back")
    public void back(Player sender) {
        if (!sender.hasMetadata(BACK_METADATA)) {
            sender.sendMessage(CC.translate("<blend:&4;&c>Couldn't find a previous location to teleport to.</>"));
            return;
        }

        Location location = (Location) sender.getMetadata(BACK_METADATA).getFirst().value();

        if (location == null) {
            sender.sendMessage(CC.translate("<blend:&4;&c>Couldn't find a previous location to teleport to.</>"));
            return;
        }

        sender.teleport(location);
        sender.sendMessage(CC.translate("&6Teleporting to your previous location..."));
    }


}
