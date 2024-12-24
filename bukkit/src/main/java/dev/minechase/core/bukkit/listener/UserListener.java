package dev.minechase.core.bukkit.listener;

import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.api.cache.UUIDCache;
import dev.minechase.core.api.log.model.impl.NewUserLog;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class UserListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID playerUUID = event.getUniqueId();
        String name = event.getName();

        CommonsAPI.getInstance().getUUIDCache().cache(event.getUniqueId(), event.getName(), !UUIDCache.getNamesToUuids().containsKey(event.getName().toLowerCase()));

        User user = CorePlugin.getInstance().getUserHandler().loadUser(playerUUID, name);

        CorePlugin.getInstance().getUserHandler().getUsers().put(playerUUID, user);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (!user.hasPlayedBefore()) {
            user.setFirstJoinAt(System.currentTimeMillis());
            new NewUserLog(player.getUniqueId()).createLog();
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());

        user.save(true);
        CorePlugin.getInstance().getUserHandler().getUsers().remove(player.getUniqueId());
    }

}
