package dev.minechase.core.bukkit.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandUtil {

    public static UUID getSender(CommandSender sender) {
        return sender instanceof Player senderPlayer ? senderPlayer.getUniqueId() : null;
    }

}
