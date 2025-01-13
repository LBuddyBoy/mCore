package dev.minechase.core.bukkit.command.impl.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameModeCommand extends BaseCommand {

    @CommandAlias("gamemode|gm")
    @CommandPermission("core.command.gamemode")
    @CommandCompletion("@gameModes @players")
    public void gamemode(CommandSender sender, @Name("gamemode") GameMode gameMode, @Name("player") @Optional AsyncCorePlayer corePlayer) {
        if (!sender.hasPermission("core.command.gamemode." + gameMode.name().toLowerCase())) {
            sender.sendMessage(CC.translate("&cNo permission."));
            return;
        }

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

        if (!sender.hasPermission("core.command.gamemode.other") && !sender.equals(player)) {
            sender.sendMessage(CC.translate("<blend:&4;&c>You lack permission to gamemode others.</>"));
            return;
        }

        player.setGameMode(gameMode);
        if (sender.equals(player)) {
            player.sendMessage(CC.translate("&6Your gamemode has been updated to &f" + gameMode.name()));
        } else {
            sender.sendMessage(CC.translate("&f" + player.getName() + "&6's gamemode updated to &f" + gameMode.name()));
        }
    }
    @CommandAlias("gmc")
    @CommandPermission("core.command.gamemode.creative")
    @CommandCompletion("@players")
    public void gmc(CommandSender sender, @Name("player") @Optional AsyncCorePlayer corePlayer) {
        gamemode(sender, GameMode.CREATIVE, corePlayer);
    }

    @CommandAlias("gms")
    @CommandPermission("core.command.gamemode.survival")
    @CommandCompletion("@players")
    public void gms(CommandSender sender, @Name("player") @Optional AsyncCorePlayer corePlayer) {
        gamemode(sender, GameMode.SURVIVAL, corePlayer);
    }

    @CommandAlias("gmspec|gmsp")
    @CommandPermission("core.command.gamemode.spectator")
    @CommandCompletion("@players")
    public void gmsp(CommandSender sender, @Name("player") @Optional AsyncCorePlayer corePlayer) {
        gamemode(sender, GameMode.SPECTATOR, corePlayer);
    }

    @CommandAlias("gma|gmadv")
    @CommandPermission("core.command.gamemode.adventure")
    @CommandCompletion("@players")
    public void gmadv(CommandSender sender, @Name("player") @Optional AsyncCorePlayer corePlayer) {
        gamemode(sender, GameMode.ADVENTURE, corePlayer);
    }

}
