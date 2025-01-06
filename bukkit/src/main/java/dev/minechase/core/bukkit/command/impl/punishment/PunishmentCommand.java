package dev.minechase.core.bukkit.command.impl.punishment;

import co.aikar.commands.BaseCommand;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.api.punishment.packet.PunishmentUpdatePacket;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.packet.GlobalMessagePacket;
import dev.minechase.core.bukkit.packet.PlayerMessagePacket;
import dev.minechase.core.bukkit.packet.StaffMessagePacket;
import dev.minechase.core.bukkit.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public abstract class PunishmentCommand extends BaseCommand {

    public abstract PunishmentType getPunishmentType();

    public void punish(CommandSender sender, UUID targetUUID, String reason, long duration, boolean shadow, boolean silent) {
        CorePlugin.getInstance().getUserHandler().getOrCreateAsync(targetUUID).whenCompleteAsync(((user, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            Punishment punishment = new Punishment(
                    sender instanceof Player senderPlayer ? senderPlayer.getUniqueId() : null,
                    targetUUID,
                    this.getPunishmentType(),
                    duration,
                    reason,
                    CorePlugin.getInstance().getServerName(),
                    shadow,
                    silent
            );

            new PunishmentUpdatePacket(punishment).send();

            String text = CommandUtil.getSenderName(sender) + "&a permanently " + getPunishmentType().getPlural() + " " + user.getColoredName() + "&a for '" + reason + "'";

            if (!punishment.isPermanent()) {
                text = CommandUtil.getSenderName(sender) + "&a temporarily " + getPunishmentType().getPlural() + " " + user.getColoredName() + "&a for '" + reason + "'";;
            }

            if (punishment.isSentSilent()) text += " &8[S]";

            if (silent) {
                new StaffMessagePacket(CC.translate(text)).send();
                return;
            }

            new GlobalMessagePacket(CC.translate(text)).send();

            if (getPunishmentType() == PunishmentType.MUTE || getPunishmentType() == PunishmentType.WARN) {
                new PlayerMessagePacket(CC.translate(Arrays.asList(
                        "&cYou have been " + getPunishmentType().getPlural() + " for '" + reason + "'",
                        "&cDuration: " + punishment.getDurationString()
                )), targetUUID).send();
            }
        }));
    }

}
