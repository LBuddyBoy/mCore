package dev.minechase.core.bukkit.util;

import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandUtil {

    public static UUID getSender(CommandSender sender) {
        return sender instanceof Player senderPlayer ? senderPlayer.getUniqueId() : null;
    }

    public static String getSenderName(CommandSender sender) {
        User senderUser = null;

        if (sender instanceof Player senderPlayer) {
            senderUser = CorePlugin.getInstance().getUserHandler().getUser(senderPlayer.getUniqueId());
        }

        return senderUser != null ? senderUser.getColoredName() : "&4&lCONSOLE";
    }

}
