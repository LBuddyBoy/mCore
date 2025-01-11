package dev.minechase.core.bukkit.listener;

import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.api.cache.UUIDCache;
import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.iphistory.model.HistoricalIP;
import dev.minechase.core.api.iphistory.packet.HistoricalIPUpdatePacket;
import dev.minechase.core.api.log.model.impl.NewUserLog;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.util.totp.TwoFactorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID playerUUID = event.getUniqueId();
        String name = event.getName();

        CommonsAPI.getInstance().getUUIDCache().cache(event.getUniqueId(), event.getName(), !UUIDCache.getNamesToUuids().containsKey(event.getName().toLowerCase()));

        User user = CorePlugin.getInstance().getUserHandler().loadUser(playerUUID, name);

        CorePlugin.getInstance().getUserHandler().getUsers().put(playerUUID, user);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());
        String ipAddress = player.getAddress().getAddress().getHostAddress();
        boolean changedIps = user.getCurrentIpAddress() != null && !user.getCurrentIpAddress().equals(ipAddress);

        CorePlugin.getInstance().getPermissionHandler().updatePermissions(player.getUniqueId());

        if (player.hasPermission(CoreConstants.STAFF_PERM)) {
            if (user.getPersistentMetadata().getBooleanOrDefault(CoreConstants.TOTP_SETUP_KEY, false) && changedIps) {
                String message = "&cPlease provide your two-factor code. Type '/auth <code>' to authenticate.";

                player.sendMessage(CC.translate(message));
                TwoFactorUtil.lock(player, CC.translate(message));
            } else if (!user.getPersistentMetadata().getBooleanOrDefault(CoreConstants.TOTP_SETUP_KEY, false)) {
                String message = "&cPlease set up your two-factor authentication using '/2fasetup'";

                player.sendMessage(CC.translate(message));
                TwoFactorUtil.lock(player, CC.translate(message));
            }
        }

        user.setCurrentIpAddress(ipAddress);

        if (changedIps) {
            CorePlugin.getInstance().getIpHistoryHandler().applyChange(player.getUniqueId(), ipAddress);
        } else {
            CorePlugin.getInstance().getIpHistoryHandler().applyLogin(player.getUniqueId(), ipAddress);
        }

        if (!user.hasPlayedBefore()) {
            user.setFirstJoinAt(System.currentTimeMillis());
            new NewUserLog(player.getUniqueId()).createLog();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());

        user.getPendingMessages().clear();

        user.save(true);
        CorePlugin.getInstance().getUserHandler().getUsers().remove(player.getUniqueId());
    }

}
