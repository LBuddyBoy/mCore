package dev.minechase.core.bukkit.api;

import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.log.model.impl.PunishmentCreationLog;
import dev.minechase.core.api.log.model.impl.PunishmentRemoveLog;
import dev.minechase.core.api.punishment.PunishmentHandler;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.api.punishment.packet.PunishmentUpdatePacket;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.packet.GlobalMessagePacket;
import dev.minechase.core.bukkit.packet.PlayerMessagePacket;
import dev.minechase.core.bukkit.packet.StaffMessagePacket;
import dev.minechase.core.bukkit.util.CommandUtil;
import dev.minechase.core.velocity.packet.PlayerKickPacket;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class BukkitPunishmentHandler extends PunishmentHandler {

    public void punish(CommandSender sender, PunishmentType type, UUID targetUUID, String reason, long duration, boolean ipRelated, boolean shadow, boolean silent) {
        sender.sendMessage(CC.translate("&aPunishing " + UUIDUtils.getName(targetUUID) + ", this may take a few seconds..."));

        CorePlugin.getInstance().getUserHandler().getOrCreateAsync(targetUUID).whenCompleteAsync(((user, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            CorePlugin.getInstance().getPunishmentHandler().getActivePunishmentsByType(targetUUID, type).whenCompleteAsync((punishments, t) -> {
                if (type != PunishmentType.KICK && type != PunishmentType.WARN) {
                    if (!punishments.isEmpty()) {
                        sender.sendMessage(CC.translate("<blend:&4;&c>That player is already " + type.getPlural() + ".</>"));
                        return;
                    }
                }

                Punishment punishment = new Punishment(
                        sender instanceof Player senderPlayer ? senderPlayer.getUniqueId() : null,
                        targetUUID,
                        type,
                        duration,
                        reason,
                        CorePlugin.getInstance().getServerName(),
                        (sender instanceof Player player ? player.getAddress().getHostName() : null),
                        user.getCurrentIpAddress(),
                        ipRelated,
                        shadow,
                        silent
                );

                new PunishmentUpdatePacket(punishment).send();

                String text = CommandUtil.getSenderName(sender) + "&a" + (type == PunishmentType.KICK || type == PunishmentType.WARN ? "" : " permanently") + " " + type.getPlural() + " " + user.getColoredName() + "&a for '" + reason + "'";

                if (!punishment.isPermanent()) {
                    text = CommandUtil.getSenderName(sender) + "&a temporarily " + type.getPlural() + " " + user.getColoredName() + "&a for '" + reason + "'";;
                }

                if (punishment.isSentSilent()) text += " &8[S]";

                new PunishmentCreationLog(text, punishment).createLog();

                if (type == PunishmentType.MUTE || type == PunishmentType.WARN) {
                    new PlayerMessagePacket(CC.translate(Arrays.asList(
                            " ",
                            "<blend:&4;&c>You have been " + type.getPlural() + " for '" + reason + "'</>",
                            "<blend:&4;&c>Duration: " + punishment.getDurationString() + "</>",
                            " "
                    )), targetUUID).send();
                } else {
                    new PlayerKickPacket(targetUUID, punishment.getKickMessage()).send();

                    CorePlugin.getInstance().getUserHandler().fetchAlts(user).whenCompleteAsync(((users, altThrowable) -> {
                        if (altThrowable != null) {
                            altThrowable.printStackTrace();
                            return;
                        }

                        for (User other : users) {
                            new PlayerKickPacket(other.getUniqueId(), punishment.getAltKickMessage(punishment.getTargetUUID())).send();
                        }
                    }));
                }

                if (silent) {
                    new StaffMessagePacket(CC.translate(text)).send();
                } else {
                    new GlobalMessagePacket(CC.translate(text)).send();
                }
            });
        }));
    }

    public void unpunish(CommandSender sender, PunishmentType type, UUID targetUUID, String reason, boolean silent) {
        sender.sendMessage(CC.translate("&aUnpunishing " + UUIDUtils.getName(targetUUID) + ", this may take a few seconds..."));

        CorePlugin.getInstance().getUserHandler().getOrCreateAsync(targetUUID).whenCompleteAsync(((user, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            CorePlugin.getInstance().getPunishmentHandler().getActivePunishmentsByType(targetUUID, type).whenCompleteAsync((punishments, t) -> {
                if (punishments.isEmpty()) {
                    sender.sendMessage(CC.translate("<blend:&4;&c>That player is not " + type.getPlural() + ".</>"));
                    return;
                }

                String text = CommandUtil.getSenderName(sender) + "&a un" + type.getPlural() + " " + user.getColoredName() + "&a for '" + reason + "'";

                if (silent) text += " &8[S]";

                for (Punishment punishment : punishments) {
                    punishment.setRemovedBy(CommandUtil.getSender(sender));
                    punishment.setRemovedAt(System.currentTimeMillis());
                    punishment.setRemovedReason(reason);
                    punishment.setRemovedSilent(silent);
                    punishment.setRemovedOn(CorePlugin.getInstance().getServerName());

                    new PunishmentUpdatePacket(punishment).send();
                    new PunishmentRemoveLog(text, punishment).createLog();
                    break;
                }

                if (silent) {
                    new StaffMessagePacket(CC.translate(text)).send();
                } else {
                    new GlobalMessagePacket(CC.translate(text)).send();
                }

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
