package dev.minechase.core.bukkit.command.impl.punishment.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.command.impl.punishment.IPunishmentCommand;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.command.CommandSender;

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

            sender.sendMessage(CC.translate("&aPunishing " + player.getName() + ", this may take a few seconds..."));

            punish(sender, uuid, actualReason, duration.transform(), false, actualReason.contains("-s"));
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

    @CommandAlias("shadowmute|smute")
    @CommandPermission("core.command.mute")
    @CommandCompletion("@players @reasons")
    public void shadowMute(CommandSender sender, @Name("player") AsyncCorePlayer player, @Name("duration") TimeDuration duration, @Name("reason {-s}") String reason) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            String actualReason = reason == null ? "None specified" : reason;

            sender.sendMessage(CC.translate("&aPunishing " + player.getName() + ", this may take a few seconds..."));

            punish(sender, uuid, actualReason, duration.transform(), true, actualReason.contains("-s"));
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

    @CommandAlias("mute")
    @CommandPermission("core.command.mute")
    @CommandCompletion("@players @reasons")
    public void perm(CommandSender sender, @Name("player") AsyncCorePlayer player, @Name("reason {-s}") String reason) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            String actualReason = reason == null ? "None specified" : reason;

            sender.sendMessage(CC.translate("&aPunishing " + player.getName() + ", this may take a few seconds..."));

            punish(sender, uuid, actualReason, -1L, false, actualReason.contains("-s"));
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

    @CommandAlias("unmute")
    @CommandPermission("core.command.unmute")
    @CommandCompletion("@players @reasons")
    public void remove(CommandSender sender, @Name("player") AsyncCorePlayer player, @Name("reason {-s}") String reason) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            String actualReason = reason == null ? "None specified" : reason;

            sender.sendMessage(CC.translate("&aUnpunishing " + player.getName() + ", this may take a few seconds..."));

            unpunish(sender, uuid, actualReason, actualReason.contains("-s"));
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

}
