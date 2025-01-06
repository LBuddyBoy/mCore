package dev.minechase.core.bukkit.listener;

import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.api.event.CoreChatEvent;
import dev.minechase.core.bukkit.settings.model.impl.GlobalChatSetting;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

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

        CorePlugin.getInstance().getPunishmentHandler().getActivePunishments(player.getUniqueId()).whenCompleteAsync(((punishments, throwable) -> {
            CoreChatEvent coreChatEvent = new CoreChatEvent(player, event.getMessage(), punishments);
            Bukkit.getPluginManager().callEvent(coreChatEvent);

            if (coreChatEvent.isCancelled()) return;

            User user = CoreAPI.getInstance().getUserHandler().getUser(player.getUniqueId());

            ChatColor chatColor = ChatColor.of(user.getPersistentMetadata().getOrDefault("chatcolor", "WHITE"));
            String displayName = user.getName();
            GlobalChatSetting setting = CorePlugin.getInstance().getSettingsHandler().getSetting(GlobalChatSetting.class);

            if (coreChatEvent.isShadowMute()) {
                player.sendMessage(CC.translate(displayName + "&7: " + chatColor) + event.getMessage());
                return;
            }

            if (!setting.isEnabled(player.getUniqueId())) {
                player.sendMessage(CC.blend("[Global Chat] We noticed you sent a message in chat, but your global chat is currently disabled. Enable by doing /tgc", "&c", "&7"));
            }

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!setting.isEnabled(online.getUniqueId())) continue;

                online.sendMessage(CC.translate(displayName + "&7: " + chatColor) + event.getMessage());
            }
        }));
    }

}
