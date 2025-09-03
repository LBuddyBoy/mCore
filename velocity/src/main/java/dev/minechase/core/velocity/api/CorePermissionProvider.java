package dev.minechase.core.velocity.api;

import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import dev.minechase.core.api.api.ScopedPermission;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.velocity.CoreVelocity;
import net.kyori.adventure.permission.PermissionChecker;

import java.util.List;

public class CorePermissionProvider implements com.velocitypowered.api.permission.PermissionProvider {

    @Override
    public PermissionFunction createFunction(PermissionSubject subject) {
        if (!(subject instanceof Player player))
            return PermissionFunction.ALWAYS_TRUE;

        return permission -> {

            User user = CoreVelocity.getInstance().getUserHandler().getUser(player.getUniqueId());

            if (user == null) return Tristate.FALSE;

            Rank rank = user.getRank();

            for (ScopedPermission scopedPermission : rank.getCombinedLocalPermissions()) {
                if (!scopedPermission.getPermissionNode().equalsIgnoreCase(permission)) continue;

                return Tristate.fromBoolean(true);
            }

            return Tristate.fromBoolean(false);
        };
    }
}
