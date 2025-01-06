package dev.minechase.core.bukkit.api;

import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.log.model.impl.PunishmentCreationLog;
import dev.minechase.core.api.log.model.impl.PunishmentRemoveLog;
import dev.minechase.core.api.punishment.PunishmentHandler;
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

public class BukkitPunishmentHandler extends PunishmentHandler {

    public void punish(CommandSender sender, PunishmentType type, UUID targetUUID, String reason, long duration, boolean shadow, boolean silent) {
        CorePlugin.getInstance().getUserHandler().getOrCreateAsync(targetUUID).whenCompleteAsync(((user, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            CorePlugin.getInstance().getPunishmentHandler().getActivePunishmentsByType(targetUUID, type).whenCompleteAsync((punishments, t) -> {
                if (!punishments.isEmpty()) {
                    sender.sendMessage(CC.translate("<blend:&4;&c>That player is already " + type.getPlural() + ".</>"));
                    return;
                }

                Punishment punishment = new Punishment(
                        sender instanceof Player senderPlayer ? senderPlayer.getUniqueId() : null,
                        targetUUID,
                        type,
                        duration,
                        reason,
                        CorePlugin.getInstance().getServerName(),
                        shadow,
                        silent
                );

                new PunishmentUpdatePacket(punishment).send();

                String text = CommandUtil.getSenderName(sender) + "&a permanently " + type.getPlural() + " " + user.getColoredName() + "&a for '" + reason + "'";

                if (!punishment.isPermanent()) {
                    text = CommandUtil.getSenderName(sender) + "&a temporarily " + type.getPlural() + " " + user.getColoredName() + "&a for '" + reason + "'";;
                }

                if (punishment.isSentSilent()) text += " &8[S]";

                if (silent) {
                    new StaffMessagePacket(CC.translate(text)).send();
                    return;
                }

                new PunishmentCreationLog(text, punishment).createLog();
                new GlobalMessagePacket(CC.translate(text)).send();

                if (type == PunishmentType.MUTE || type == PunishmentType.WARN) {
                    new PlayerMessagePacket(CC.translate(Arrays.asList(
                            " ",
                            "<blend:&4;&c>You have been " + type.getPlural() + " for '" + reason + "'</>",
                            "<blend:&4;&c>Duration: " + punishment.getDurationString() + "</>",
                            " "
                    )), targetUUID).send();
                }
            });
        }));
    }

    public void unpunish(CommandSender sender, PunishmentType type, UUID targetUUID, String reason, boolean silent) {
        CorePlugin.getInstance().getUserHandler().getOrCreateAsync(targetUUID).whenCompleteAsync(((user, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            CorePlugin.getInstance().getPunishmentHandler().getActivePunishmentsByType(targetUUID, type).whenCompleteAsync((punishments, t) -> {
                if (punishments.isEmpty()) {
                    sender.sendMessage(CC.translate("<blend:&4;&c>That player is already " + type.getPlural() + ".</>"));
                    return;
                }

                String text = CommandUtil.getSenderName(sender) + "&a un" + type.getPlural() + " " + user.getColoredName() + "&a for '" + reason + "'";

                if (silent) text += " &8[S]";

                if (silent) {
                    new StaffMessagePacket(CC.translate(text)).send();
                    return;
                }

                for (Punishment punishment : punishments) {
                    punishment.setRemovedBy(CommandUtil.getSender(sender));
                    punishment.setRemovedAt(System.currentTimeMillis());
                    punishment.setRemovedReason(reason);
                    punishment.setRemovedSilent(silent);

                    new PunishmentUpdatePacket(punishment).send();
                    new PunishmentRemoveLog(text, punishment).createLog();
                }

                new GlobalMessagePacket(CC.translate(text)).send();

                if (type == PunishmentType.MUTE || type == PunishmentType.WARN) {
                    new PlayerMessagePacket(
                            CC.translate("<blend:&4;&c>You have been un" + type.getPlural() + " for '" + reason + "'</>"),
                            targetUUID
                    ).send();
                }

            });
        }));
    }


}
