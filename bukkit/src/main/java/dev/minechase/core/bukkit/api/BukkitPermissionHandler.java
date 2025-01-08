package dev.minechase.core.bukkit.api;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.IExpirable;
import dev.minechase.core.api.api.IScoped;
import dev.minechase.core.api.api.ScopedPermission;
import dev.minechase.core.api.permission.PermissionHandler;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BukkitPermissionHandler extends PermissionHandler {

    @Override
    public void updatePermissions(UUID playerUUID) {
        this.getPermissions(playerUUID).whenCompleteAsync((permissionMap, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            Player player = Bukkit.getPlayer(playerUUID);
            User user = CorePlugin.getInstance().getUserHandler().getUser(playerUUID);

            if (player == null) {
                CoreAPI.getInstance().getLogger().warning("Tried updating " + UUIDUtils.getName(playerUUID) + " permissions, but they aren't online.");
                return;
            }

            if (user == null) {
                CoreAPI.getInstance().getLogger().warning("Tried updating " + UUIDUtils.getName(playerUUID) + " permissions, but couldn't find their profile.");
                return;
            }

            List<ScopedPermission> permissions = permissionMap.values().stream().filter(IExpirable::isActive).filter(IScoped::isValidLocal).toList();

            for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
                if (attachmentInfo.getAttachment() == null) continue;

                attachmentInfo.getAttachment().getPermissions().forEach((permission, value) -> attachmentInfo.getAttachment().unsetPermission(permission));
            }

            PermissionAttachment attachment = player.addAttachment(CorePlugin.getInstance());

            if (player.isOp()) attachment.setPermission("op", true);

            for (ScopedPermission permission : permissions) {
                attachment.setPermission(permission.getPermissionNode(), true);
            }

            Rank rank = user.getRank();

            if (rank == null) {
                CoreAPI.getInstance().getLogger().warning("Tried updating " + UUIDUtils.getName(playerUUID) + " permissions, but couldn't find a rank.");
                return;
            }

            for (ScopedPermission permission : rank.getCombinedLocalPermissions()) {
                attachment.setPermission(permission.getPermissionNode(), true);
            }
        });
    }
}
