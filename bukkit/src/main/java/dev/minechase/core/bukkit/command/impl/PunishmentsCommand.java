package dev.minechase.core.bukkit.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.menu.ViewGrantsMenu;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.entity.Player;

@CommandAlias("punishments|c|check|history|hist")
@CommandPermission("core.command.punishments")
public class PunishmentsCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public void def(Player sender, @Name("player") AsyncCorePlayer player) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            sender.sendMessage(CC.translate("&aLoading " + player.getName() + "'s punishments, this may take a few seconds..."));

            CorePlugin.getInstance().getPunishmentHandler().getPunishments(uuid).whenCompleteAsync(((punishments, throwable) -> {
                for (Punishment punishment : punishments) {
                    sender.sendMessage(CC.translate("&c" + punishment.getType().name() + " - " + (punishment.isActive() ? "&aActive" : "&cInactive")));
                }
            }));

        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

}
