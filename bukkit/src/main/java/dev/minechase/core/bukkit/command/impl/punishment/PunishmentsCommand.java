package dev.minechase.core.bukkit.command.impl.punishment;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.menu.punishments.PunishmentsMainMenu;
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
                new PunishmentsMainMenu(uuid, punishments).openMenu(sender);
            }));

        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

}
