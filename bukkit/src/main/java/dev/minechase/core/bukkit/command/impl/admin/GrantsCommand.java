package dev.minechase.core.bukkit.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.api.MultiScope;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.menu.ViewGrantsMenu;
import dev.minechase.core.bukkit.menu.ViewPermissionsMenu;
import dev.minechase.core.bukkit.menu.grant.GrantRankMenu;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantsCommand extends BaseCommand {

    @CommandAlias("grants")
    @CommandPermission("core.command.grants")
    @CommandCompletion("@players")
    public void grants(Player sender, @Name("player") AsyncCorePlayer player) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            sender.sendMessage(CC.translate("&aLoading " + player.getName() + "'s rank grants, this may take a few seconds..."));

            CorePlugin.getInstance().getGrantHandler().getSortedGrants(uuid).whenCompleteAsync(((grants, throwable) -> {
                new ViewGrantsMenu(uuid, grants).openMenu(sender);
            }));

        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));

    }

    @CommandAlias("pgrants")
    @CommandPermission("core.command.grants")
    @CommandCompletion("@players")
    public void pgrants(Player sender, @Name("player") AsyncCorePlayer player) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            sender.sendMessage(CC.translate("&aLoading " + player.getName() + "'s permission grants, this may take a few seconds..."));

            CorePlugin.getInstance().getPermissionHandler().getPermissions(uuid).whenCompleteAsync(((permissions, throwable) -> {
                new ViewPermissionsMenu(uuid, permissions.values().stream().toList()).openMenu(sender);
            }));

        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));

    }

    @CommandAlias("grant")
    @CommandCompletion("@players")
    @CommandPermission("core.command.grant")
    public void grant(Player sender, @Name("player") AsyncCorePlayer player) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            new GrantRankMenu(uuid).openMenu(sender);
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));

    }

    @CommandAlias("ogrant")
    @CommandCompletion("@players @ranks @durations @scopes")
    @CommandPermission("core.command.grant")
    public void ogrant(CommandSender sender, @Name("player") AsyncCorePlayer player, @Name("rank") Rank rank, @Name("duration") TimeDuration duration, @Name("scopes") MultiScope scope, @Name("reason") String reason) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            CorePlugin.getInstance().getGrantHandler().grant(sender, uuid, rank, scope.getScopes(), duration.transform(), reason);
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));

    }

    @CommandAlias("pgrant")
    @CommandCompletion("@players <permission> @durations @scopes")
    @CommandPermission("core.command.pgrant")
    public void pgrant(CommandSender sender, @Name("player") AsyncCorePlayer player, @Name("permission") String permission, @Name("duration") TimeDuration duration, @Name("scopes") MultiScope scope, @Name("reason") String reason) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            CorePlugin.getInstance().getGrantHandler().grantPermission(sender, uuid, permission, scope.getScopes(), duration.transform(), reason);
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));

    }

}
