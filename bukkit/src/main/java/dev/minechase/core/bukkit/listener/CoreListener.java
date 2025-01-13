package dev.minechase.core.bukkit.listener;

import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.api.event.AsyncCoreChatEvent;
import dev.minechase.core.bukkit.api.event.AsyncCoreLoginEvent;
import dev.minechase.core.bukkit.packet.StaffMessagePacket;
import dev.minechase.core.bukkit.settings.model.impl.AdminChatSetting;
import dev.minechase.core.bukkit.settings.model.impl.GlobalChatSetting;
import dev.minechase.core.bukkit.settings.model.impl.StaffChatSetting;
import dev.minechase.core.bukkit.util.FilterUtil;
import dev.minechase.core.velocity.packet.PlayerKickPacket;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

import java.util.UUID;

/**
 * Overrides Bukkit Events to ensure proper cache calls
 */

public class CoreListener implements Listener {

    /**
     *
     * Override the Bukkit API Chat Event to ensure proper use of the core cache.
     *
     * @param event bukkit chat event
     */

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        User user = CoreAPI.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (FilterUtil.isDisallowed(CC.stripColor(CC.translate(event.getMessage())))) {
            event.setCancelled(true);
            player.sendMessage(CC.translate("&c[Filter] A message you sent was filtered."));
            new StaffMessagePacket("&4[Filtered Message] &c(" + CoreAPI.getInstance().getServerName() + ") &b" + user.getName() + "&7: " + ChatColor.stripColor(event.getMessage())).send();
            return;
        }

        if (user == null) {
            player.sendMessage(CC.translate("<blend:&4;&c>Error loading your profile, please contact an admin.</>"));
            return;
        }

        if (player.hasPermission("core.command.adminchat") && CorePlugin.getInstance().getSettingsHandler().getSetting(AdminChatSetting.class).isEnabled(player.getUniqueId())) {
            new StaffMessagePacket(CC.translate("&c[Admin Chat] " + user.getDisplayName() + "&7: &f") + event.getMessage()).send();
            return;
        }

        if (player.hasPermission("core.command.staffchat") && CorePlugin.getInstance().getSettingsHandler().getSetting(StaffChatSetting.class).isEnabled(player.getUniqueId())) {
            new StaffMessagePacket(CC.translate("&9[Staff Chat] " + user.getDisplayName() + "&7: &f") + event.getMessage()).send();
            return;
        }

        if (!player.hasPermission("core.chat.bypass") && CorePlugin.getInstance().getChatHandler().isMuted()) {
            player.sendMessage(CC.translate("<blend:&4;&c>Chat is muted.</>"));
            return;
        }

        if (!player.hasPermission("core.chat.bypass") && CorePlugin.getInstance().getChatHandler().isSlowed()) {
            long timeLeft = user.getLocalMetadata().getLong("slowChat") - System.currentTimeMillis();

            if (timeLeft >= 0) {
                player.sendMessage(CC.translate("<blend:&4;&c>Chat is slowed. You may talk in " + (timeLeft / 1000L) + " seconds.</>"));
                return;
            }

            user.getLocalMetadata().setLong("slowChat", System.currentTimeMillis() + (CorePlugin.getInstance().getChatHandler().getSlowedDelay() * 1000L));
        }

        CorePlugin.getInstance().getPunishmentHandler().fetchSnapshotsRelating(player.getUniqueId(), user.getCurrentIpAddress()).whenCompleteAsync(((punishments, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            AsyncCoreChatEvent asyncCoreChatEvent = new AsyncCoreChatEvent(player, event.getMessage(), punishments);
            Bukkit.getPluginManager().callEvent(asyncCoreChatEvent);

            if (asyncCoreChatEvent.isCancelled()) return;

            ChatColor chatColor = ChatColor.of(user.getPersistentMetadata().getOrDefault("chatcolor", "WHITE"));
            GlobalChatSetting setting = CorePlugin.getInstance().getSettingsHandler().getSetting(GlobalChatSetting.class);

            if (asyncCoreChatEvent.isShadowMute()) {
                player.sendMessage(CC.translate(user.getChatDisplay() + "&7: " + chatColor) + event.getMessage());
                return;
            }

            if (!setting.isEnabled(player.getUniqueId())) {
                player.sendMessage(CC.blend("[Global Chat] We noticed you sent a message in chat, but your global chat is currently disabled. Enable by doing /tgc", "&c", "&7"));
            }

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!setting.isEnabled(online.getUniqueId())) continue;

                online.sendMessage(CC.translate(user.getChatDisplay() + "&7: " + chatColor) + event.getMessage());
            }
        }));
    }

    /**
     *
     * Override the Bukkit API Login Event to ensure proper use of the core cache.
     *
     * @param event bukkit chat event
     */

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = CoreAPI.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (user == null) {
            player.sendMessage(CC.translate("<blend:&4;&c>Error loading your profile, please contact an admin.</>"));
            player.kickPlayer(CC.translate("<blend:&4;&c>Error loading your profile, please contact an admin.</>"));
            return;
        }

        CorePlugin.getInstance().getPunishmentHandler().fetchSnapshotsRelating(player.getUniqueId(), user.getCurrentIpAddress()).whenCompleteAsync(((punishments, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            AsyncCoreLoginEvent asyncCoreLoginEvent = new AsyncCoreLoginEvent(player.getUniqueId(), player.getName(), punishments);
            Bukkit.getPluginManager().callEvent(asyncCoreLoginEvent);

            if (!asyncCoreLoginEvent.isCancelled()) {
                return;
            }

            new PlayerKickPacket(player.getUniqueId(), (asyncCoreLoginEvent.getKickMessage() == null ? "None provided" : asyncCoreLoginEvent.getKickMessage())).send();
        }));
    }

}
