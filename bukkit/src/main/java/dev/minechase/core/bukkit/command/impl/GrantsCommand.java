package dev.minechase.core.bukkit.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.menu.ViewGrantsMenu;
import dev.minechase.core.bukkit.menu.grant.GrantRankMenu;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.entity.Player;

public class GrantsCommand extends BaseCommand {

    @CommandAlias("grants")
    @CommandPermission("core.command.grants")
    @CommandCompletion("@players")
    public void grants(Player sender, @Name("player") AsyncCorePlayer player) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            sender.sendMessage(CC.translate("&aLoading " + player.getName() + "'s grants, this may take a few seconds..."));

            CorePlugin.getInstance().getGrantHandler().getSortedGrants(uuid).whenCompleteAsync(((grants, throwable) -> {
                new ViewGrantsMenu(uuid, grants).openMenu(sender);
            }));

        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));

    }

    @CommandAlias("grant")
    @CommandCompletion("@players")
    public void grant(Player sender, @Name("player") AsyncCorePlayer player) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {

            new GrantRankMenu(uuid).openMenu(sender);

        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));

    }

}
