package dev.minechase.core.bukkit.api;

import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.api.MultiScope;
import dev.minechase.core.api.grant.GrantHandler;
import dev.minechase.core.api.grant.grant.Grant;
import dev.minechase.core.api.grant.packet.GrantUpdatePacket;
import dev.minechase.core.api.log.model.impl.GrantCreationLog;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.util.UUIDUtils;
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
                reason,
                new MultiScope(scopesString)
        );

        new GrantUpdatePacket(grant).send();
        new GrantCreationLog(grant).createLog();

        sender.sendMessage(CC.translate("&aSuccessfully granted " + grant.getTargetName() + " the &f" + rank.getName() + "&a rank for &e" + grant.getDurationString() + "."));
    }

}
