package dev.minechase.core.bukkit.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.menu.ViewGrantsMenu;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("alts")
@CommandPermission("core.command.alts")
public class AltsCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public void def(CommandSender sender, @Name("player") AsyncCorePlayer player) {
        player.getUser().whenCompleteAsyncExcept(user -> {
            sender.sendMessage(CC.translate("&aLoading " + player.getName() + "'s alts, this may take a few seconds..."));

            CorePlugin.getInstance().getUserHandler().fetchUsersAsync().whenCompleteAsync(((users, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    sender.sendMessage(CC.translate("&cError loading " + player.getName() + "'s alts, check console for more info..."));
                    return;
                }

                users = users.stream().filter(other -> user.getCurrentIpAddress() != null && other.getCurrentIpAddress() != null && other.getCurrentIpAddress().equals(user.getCurrentIpAddress())).toList();

                for (User altUser : users) {
                    sender.sendMessage(CC.translate("&a" + altUser.getName()));
                }
            }));

        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

}
