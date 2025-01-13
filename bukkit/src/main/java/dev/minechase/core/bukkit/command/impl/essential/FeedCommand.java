package dev.minechase.core.bukkit.command.impl.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import dev.minechase.core.bukkit.util.CommandUtil;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand extends BaseCommand {

    @CommandAlias("feed|eat")
    @CommandPermission("core.command.feed")
    @CommandCompletion("@players")
    public void feed(CommandSender sender, @Name("player") @Optional AsyncCorePlayer corePlayer) {
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

        if (!sender.hasPermission("core.command.feed.other") && !sender.equals(player)) {
            sender.sendMessage(CC.translate("<blend:&4;&c>You lack permission to feed others.</>"));
            return;
        }

        player.setFoodLevel(20);

        if (sender.equals(player)) {
            player.sendMessage(CC.translate("<blend:&2;&a>You have fed yourself.</>"));
        } else {
            sender.sendMessage(CC.translate("<blend:&2;&a>You have fed " + player.getName() + ".</>"));
            player.sendMessage(CC.translate("<blend:&2;&a>" + CommandUtil.getSenderName(sender) + " has fed you.</>"));
        }
    }

    @CommandAlias("heal")
    @CommandPermission("core.command.heal")
    @CommandCompletion("@players")
    public void heal(CommandSender sender, @Name("player") @Optional AsyncCorePlayer corePlayer) {
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

        if (!sender.hasPermission("core.command.feed.other") && !sender.equals(player)) {
            sender.sendMessage(CC.translate("<blend:&4;&c>You lack permission to feed others.</>"));
            return;
        }

        player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());

        if (sender.equals(player)) {
            player.sendMessage(CC.translate("<blend:&2;&a>You have healed yourself.</>"));
        } else {
            sender.sendMessage(CC.translate("<blend:&2;&a>You have healed " + player.getName() + ".</>"));
            player.sendMessage(CC.translate("<blend:&2;&a>" + CommandUtil.getSenderName(sender) + " has healed you.</>"));
        }
    }

}
