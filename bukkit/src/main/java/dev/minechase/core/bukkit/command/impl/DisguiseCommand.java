package dev.minechase.core.bukkit.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.iiahmed.disguise.Disguise;
import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import dev.iiahmed.disguise.SkinAPI;
import dev.lbuddyboy.commons.Commons;
import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.menu.ViewGrantsMenu;
import dev.minechase.core.bukkit.menu.disguise.DisguiseNameMenu;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import dev.minechase.core.bukkit.packet.StaffMessagePacket;
import dev.minechase.core.bukkit.packet.UnDisguisePacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("disguise")
@CommandPermission("core.command.disguise")
public class DisguiseCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        new DisguiseNameMenu().openMenu(sender);
    }

    @Subcommand("admin disguise")
    @CommandPermission("core.command.disguise.admin")
    @CommandCompletion("@players @ranks")
    public void adminDisguise(Player sender, @Name("name") AsyncCorePlayer player, @Name("rank") Rank rank) {
        User user = CorePlugin.getInstance().getUserHandler().getUser(sender.getUniqueId());

        if (user.isDisguised()) {
            sender.sendMessage(CC.translate("<blend:&4;&c>You are already disguised.</>"));
            return;
        }

        player.getUUID().whenCompleteExcept(uuid -> {
            CorePlugin.getInstance().getUserHandler().disguise(sender, Disguise.builder().setName(player.getName()).setSkin(SkinAPI.MOJANG, uuid), rank);

            sender.sendMessage(CC.translate("<blend:&2;&a>You are now admin disguised as " + player.getName() + ".</>"));
        }, (throwable -> sender.sendMessage(CC.translate("<blend:&4;&c>No player with the name '" + player.getName() + "' exists.</>"))));
    }

    @Subcommand("admin undisguise")
    @CommandPermission("core.command.disguise.admin")
    @CommandCompletion("@players")
    public void undisguise(Player sender, @Name("player") AsyncCorePlayer player) {
        player.getUser().whenCompleteExcept(user -> {
            if (!user.isDisguised()) {
                sender.sendMessage(CC.translate("<blend:&4;&c>" + user.getName() + " is not disguised.</>"));
                return;
            }

            new UnDisguisePacket(sender.getUniqueId(), user.getUniqueId()).send();
            new StaffMessagePacket("&4[Disguise] &4" + UUIDUtils.getName(sender.getUniqueId()) + "&c removed &e" + user.getName() + "'s&c disguise.").send();
        }, (throwable -> sender.sendMessage(CC.translate("<blend:&4;&c>No player with the name '" + player.getName() + "' exists.</>"))));
    }

}
