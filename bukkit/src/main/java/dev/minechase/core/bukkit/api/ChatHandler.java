package dev.minechase.core.bukkit.api;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatHandler implements IModule {

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

        CorePlugin.getInstance().getConfig().set("chat.slowed.enabled", true);
        CorePlugin.getInstance().getConfig().set("chat.slowed.secondsDelay", secondsDelay);
        CorePlugin.getInstance().getConfig().set("chat.slowed.duration", duration);
        CorePlugin.getInstance().getConfig().set("chat.slowed.mutedAt", System.currentTimeMillis());
        CorePlugin.getInstance().saveConfig();
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

        CorePlugin.getInstance().getConfig().set("chat.slowed.enabled", false);
        CorePlugin.getInstance().getConfig().set("chat.slowed.duration", -1L);
        CorePlugin.getInstance().getConfig().set("chat.slowed.mutedAt", 0L);
        CorePlugin.getInstance().saveConfig();
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

    public long getMutedAt() {
        return CorePlugin.getInstance().getConfig().getLong("chat.muted.mutedAt");
    }

    public long getMutedDuration() {
        return CorePlugin.getInstance().getConfig().getLong("chat.muted.duration");
    }

    public boolean isMuted() {
        if (getMutedDuration() <= -1L) return CorePlugin.getInstance().getConfig().getBoolean("chat.muted.enabled");

        return System.currentTimeMillis() - getMutedAt() < getMutedDuration() && CorePlugin.getInstance().getConfig().getBoolean("chat.muted.enabled");
    }

    public long getSlowedAt() {
        return CorePlugin.getInstance().getConfig().getLong("chat.slowed.slowedAt");
    }

    public int getSlowedDelay() {
        return CorePlugin.getInstance().getConfig().getInt("chat.slowed.secondsDelay");
    }

    public long getSlowedDuration() {
        return CorePlugin.getInstance().getConfig().getLong("chat.slowed.duration");
    }

    public boolean isSlowed() {
        if (getSlowedDuration() <= -1L) return CorePlugin.getInstance().getConfig().getBoolean("chat.slowed.enabled");

        return System.currentTimeMillis() - getSlowedAt() < getSlowedDuration() && CorePlugin.getInstance().getConfig().getBoolean("chat.slowed.enabled");
    }

}
