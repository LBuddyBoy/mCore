package dev.minechase.core.bukkit.listener;

import com.mojang.authlib.properties.Property;
import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.api.cache.UUIDCache;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.log.model.impl.NewUserLog;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.user.packet.UserUpdatePacket;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.command.impl.essential.TeleportCommand;
import dev.minechase.core.bukkit.util.totp.TwoFactorUtil;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class UserListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID playerUUID = event.getUniqueId();
        String name = event.getName();
        User user = CorePlugin.getInstance().getUserHandler().getCache().loadUser(playerUUID);

        System.out.printf("User: %s", user);

        user.setName(name);

        CorePlugin.getInstance().getUserHandler().getUsers().put(playerUUID, user);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());
        String ipAddress = player.getAddress().getAddress().getHostAddress();
        boolean changedIps = user.getCurrentIpAddress() != null && !user.getCurrentIpAddress().equals(ipAddress);
        Property property = ((CraftPlayer) player).getProfile().getProperties().get("textures").stream().findFirst().orElse(null);
        boolean save = false;

        if (property != null) {
            user.getPersistentMetadata().set(User.HEAD_TEXTURE_KEY, property.value());
            user.getPersistentMetadata().set(User.HEAD_SIGNATURE_KEY, property.signature());
            save = true;
        }

        CorePlugin.getInstance().getUserHandler().updateDisguise(player);

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
            save = true;
        } else {
            CorePlugin.getInstance().getIpHistoryHandler().applyLogin(player.getUniqueId(), ipAddress);
        }

        if (save) user.save();

        CorePlugin.getInstance().getServerHandler().getPlayerLocationStorage().saveAsync(
                player.getUniqueId(),
                CorePlugin.getInstance().getServerName()
        );
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());

        user.getPendingMessages().clear();

        new UserUpdatePacket(user).send();

        CorePlugin.getInstance().getUserHandler().getUsers().remove(player.getUniqueId());
        CorePlugin.getInstance().getServerHandler().getPlayerLocationStorage().deleteAsync(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        player.setMetadata(TeleportCommand.BACK_METADATA, new FixedMetadataValue(CorePlugin.getInstance(), event.getTo()));
    }

}
