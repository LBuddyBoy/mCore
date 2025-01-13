package dev.minechase.core.bukkit.command.impl.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import dev.minechase.core.bukkit.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClearCommand extends BaseCommand {

    @CommandAlias("clear|ci|clearinventory")
    @CommandPermission("core.command.clear")
    @CommandCompletion("@players")
    public void clear(CommandSender sender, @Name("player") @Optional AsyncCorePlayer corePlayer) {
        if (!(sender instanceof Player senderPlayer)) {
            if (corePlayer == null) {
                sender.sendMessage(CC.translate("<blend:&4;&c>Please provide a player name.</>"));
                return;
            }
        } else {
            if (corePlayer == null) corePlayer = new AsyncCorePlayer(senderPlayer.getName());
        }

        Player player = corePlayer.getPlayer();

        if (player == null) {
            sender.sendMessage(CC.translate("<blend:&4;&c>That player is not online.</>"));
            return;
        }

        if (!sender.hasPermission("core.command.clear.other") && !sender.equals(player)) {
            sender.sendMessage(CC.translate("<blend:&4;&c>You lack permission to clear others.</>"));
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        if (sender.equals(player)) {
            player.sendMessage(CC.translate("<blend:&2;&a>You have cleared your inventory.</>"));
        } else {
            sender.sendMessage(CC.translate("&6You have cleared &f" + player.getName() + "&6's inventory."));
            player.sendMessage(CC.translate("&f" + CommandUtil.getSenderName(sender) + " &6has cleared your inventory."));
        }
    }

}
