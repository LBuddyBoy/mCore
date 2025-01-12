package dev.minechase.core.bukkit.api;

import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.MultiScope;
import dev.minechase.core.api.api.ScopedPermission;
import dev.minechase.core.api.grant.GrantHandler;
import dev.minechase.core.api.grant.model.Grant;
import dev.minechase.core.api.grant.packet.GrantUpdatePacket;
import dev.minechase.core.api.log.model.impl.grant.GrantCreationLog;
import dev.minechase.core.api.log.model.impl.permission.PermissionCreationLog;
import dev.minechase.core.api.permission.packet.PermissionUpdatePacket;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.packet.PlayerMessagePacket;
import dev.minechase.core.bukkit.util.CommandUtil;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class BukkitGrantHandler extends GrantHandler {

    public void grant(CommandSender sender, UUID targetUUID, Rank rank, List<String> scopes, long duration, String reason) {
        String scopesString = StringUtils.join(scopes, ",");

        Grant grant = new Grant(
                CommandUtil.getSender(sender),
                targetUUID,
                rank,
                duration,
                CoreAPI.getInstance().getServerName(),
                reason,
                new MultiScope(scopesString)
        );

        new GrantUpdatePacket(grant).send();
        new GrantCreationLog(grant).createLog();
        new PlayerMessagePacket(
                CC.translate("&aYou were granted the " + rank.getDisplayName() + "&a rank for &e" + (grant.isPermanent() ? "Forever" : TimeUtils.formatIntoDetailedString(duration))),
                targetUUID
        ).send();

        sender.sendMessage(CC.translate("&aSuccessfully granted " + grant.getTargetName() + " the &f" + rank.getName() + "&a rank for &e" + grant.getDurationString() + "."));

    }

    public void grantPermission(CommandSender sender, UUID targetUUID, String permissionNode, List<String> scopes, long duration, String reason) {
        String scopesString = StringUtils.join(scopes, ",");

        ScopedPermission permission = new ScopedPermission(
                CommandUtil.getSender(sender),
                targetUUID,
                permissionNode,
                duration,
                CoreAPI.getInstance().getServerName(),
                reason,
                new MultiScope(scopesString)
        );

        new PermissionUpdatePacket(permission).send();
        new PermissionCreationLog(permission).createLog();
        new PlayerMessagePacket(
                CC.translate("&aYou were granted the &e" + permission.getPermissionNode() + "&a rank for &e" + (permission.isPermanent() ? "Forever" : TimeUtils.formatIntoDetailedString(duration))),
                targetUUID
        ).send();

        sender.sendMessage(CC.translate("&aSuccessfully granted " + permission.getTargetName() + " the &f" + permission.getPermissionNode() + "&a rank for &e" + permission.getDurationString() + "."));
    }

    @Override
    public void onRankChange(User user, Grant previousGrant, Grant newGrant) {
        super.onRankChange(user, previousGrant, newGrant);
        // TODO: Add discord sync here
    }
}
