package dev.minechase.core.bukkit.command.impl.punishment.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.api.punishment.packet.PunishmentUpdatePacket;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.command.impl.punishment.IPunishmentCommand;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand extends BaseCommand implements IPunishmentCommand {

    @Override
    public PunishmentType getPunishmentType() {
        return PunishmentType.MUTE;
    }

    @CommandAlias("tmute|tempmute")
    @CommandPermission("core.command.mute")
    @CommandCompletion("@players @reasons")
    public void temp(CommandSender sender, @Name("player") AsyncCorePlayer player, @Name("duration") TimeDuration duration, @Name("reason {-s}") String reason) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            String actualReason = reason == null ? "None specified" : reason;

            punish(sender, uuid, actualReason, duration.transform(), false, actualReason.contains("-s"));
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

    @CommandAlias("tshadowmute|tempshowmute")
    @CommandPermission("core.command.tempshadowmute")
    @CommandCompletion("@players @reasons")
    public void tempShadow(CommandSender sender, @Name("player") AsyncCorePlayer player, @Name("duration") TimeDuration duration, @Name("reason {-s}") String reason) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            String actualReason = reason == null ? "None specified" : reason;

            punish(sender, uuid, actualReason, duration.transform(), true, actualReason.contains("-s"));
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

    @CommandAlias("shadowmute|smute")
    @CommandPermission("core.command.shadowmute")
    @CommandCompletion("@players @reasons")
    public void shadowMute(CommandSender sender, @Name("player") AsyncCorePlayer player, @Name("duration") TimeDuration duration, @Name("reason {-s}") String reason) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            String actualReason = reason == null ? "None specified" : reason;

            punish(sender, uuid, actualReason, duration.transform(), true, actualReason.contains("-s"));
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

    @CommandAlias("mute")
    @CommandPermission("core.command.mute")
    @CommandCompletion("@players @reasons")
    public void perm(CommandSender sender, @Name("player") AsyncCorePlayer player, @Name("reason {-s}") String reason) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            String actualReason = reason == null ? "None specified" : reason;

            punish(sender, uuid, actualReason, Long.MAX_VALUE, false, actualReason.contains("-s"));
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

    @CommandAlias("unmute")
    @CommandPermission("core.command.unmute")
    @CommandCompletion("@players @reasons")
    public void remove(CommandSender sender, @Name("player") AsyncCorePlayer player, @Name("reason {-s}") String reason) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            String actualReason = reason == null ? "None specified" : reason;

            unpunish(sender, uuid, actualReason, actualReason.contains("-s"));
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

    @CommandAlias("voidmutes")
    @CommandPermission("core.command.voidmutes")
    public void voidbans(CommandSender sender, @Name("reason") String reason) {
        if (sender instanceof Player) {
            return;
        }

        long startedAt = System.currentTimeMillis();

        sender.sendMessage(CC.translate("&aVoiding mutes..."));

        CorePlugin.getInstance().getPunishmentHandler().fetchAllPunishments().whenCompleteAsync(((punishments, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            punishments = punishments.stream().filter(punishment -> punishment.isActive() && punishment.getType() == PunishmentType.MUTE).toList();

            for (Punishment punishment : punishments) {
                punishment.remove(null, reason);

                new PunishmentUpdatePacket(punishment).send();
            }

            sender.sendMessage(CC.translate("&aFinished voiding " + punishments.size() + " mutes in " + (System.currentTimeMillis() - startedAt) + " ms"));
        }));
    }

}
