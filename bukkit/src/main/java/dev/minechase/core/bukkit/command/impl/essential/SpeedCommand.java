package dev.minechase.core.bukkit.command.impl.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import dev.minechase.core.bukkit.util.CommandUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand extends BaseCommand {

    @CommandAlias("speed|walkspeed|ws")
    @CommandPermission("core.command.speed")
    @CommandCompletion("@players")
    public void feed(CommandSender sender, @Name("speed") float speed, @Name("player") @Optional AsyncCorePlayer corePlayer) {
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

        if (!sender.hasPermission("core.command.speed.other") && !sender.equals(player)) {
            sender.sendMessage(CC.translate("<blend:&4;&c>You lack permission to speed others.</>"));
            return;
        }

        player.setWalkSpeed(speed);

        if (sender.equals(player)) {
            player.sendMessage(CC.translate("&6You have set your walk speed to: " + speed + ".</>"));
        } else {
            sender.sendMessage(CC.translate("&6You have set " + player.getName() + "'s walk speed to " + speed + "."));
            player.sendMessage(CC.translate("&f" + CommandUtil.getSenderName(sender) + " &6has set your speed to " + speed + "."));
        }
    }

}
