package dev.minechase.core.bukkit.command.impl.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.menu.ViewGrantsMenu;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.entity.Player;

@CommandAlias("iphistory")
@CommandPermission("core.command.iphistory")
public class IPHistoryCommand extends BaseCommand {

    @Default
    public void def(Player sender, @Name("player") AsyncCorePlayer player) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            sender.sendMessage(CC.translate("&aLoading " + player.getName() + "'s ip history, this may take a few seconds..."));

            CorePlugin.getInstance().getIpHistoryHandler().getHistoricalIps(uuid).whenCompleteAsync((ips, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    sender.sendMessage(CC.translate("&cError loading " + player.getName() + "'s ip history, check console for more info..."));
                    return;
                }


            });

        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));

    }

}
