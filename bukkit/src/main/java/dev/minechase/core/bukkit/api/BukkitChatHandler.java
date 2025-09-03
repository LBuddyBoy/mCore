package dev.minechase.core.bukkit.api;

import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.chat.ChatHandler;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitChatHandler extends ChatHandler {

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }

    public void slowChat(CommandSender sender, int secondsDelay, long duration) {
        String senderName = "";

        if (sender instanceof Player player) {
            User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());

            senderName = user.getColoredName();
        } else {
            senderName = "&4&lCONSOLE";
        }

        Bukkit.broadcastMessage(CC.translate(senderName + "&a slowed the chat."));

        this.slowChat(secondsDelay, duration);
    }

    public void unslowChat(CommandSender sender) {
        String senderName = "";

        if (sender instanceof Player player) {
            User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());

            senderName = user.getColoredName();
        } else {
            senderName = "&4&lCONSOLE";
        }

        Bukkit.broadcastMessage(CC.translate(senderName + "&a unslowed the chat."));

        this.unslowChat();
    }

    public void mute(CommandSender sender, long duration) {
        String senderName = "";

        if (sender instanceof Player player) {
            User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());

            senderName = user.getColoredName();
        } else {
            senderName = "&4&lCONSOLE";
        }

        Bukkit.broadcastMessage(CC.translate(senderName + "&a muted the chat."));

        CorePlugin.getInstance().getConfig().set("chat.muted.enabled", true);
        CorePlugin.getInstance().getConfig().set("chat.muted.duration", duration);
        CorePlugin.getInstance().getConfig().set("chat.muted.mutedAt", System.currentTimeMillis());
        CorePlugin.getInstance().saveConfig();
    }

    public void unmute(CommandSender sender) {
        String senderName = "";

        if (sender instanceof Player player) {
            User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());

            senderName = user.getColoredName();
        } else {
            senderName = "&4&lCONSOLE";
        }

        Bukkit.broadcastMessage(CC.translate(senderName + "&a unmuted the chat."));

        CorePlugin.getInstance().getConfig().set("chat.muted.enabled", false);
        CorePlugin.getInstance().getConfig().set("chat.muted.duration", -1L);
        CorePlugin.getInstance().getConfig().set("chat.muted.mutedAt", 0L);
        CorePlugin.getInstance().saveConfig();
    }

}
