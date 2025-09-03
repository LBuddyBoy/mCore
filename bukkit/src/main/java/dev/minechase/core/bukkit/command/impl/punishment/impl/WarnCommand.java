package dev.minechase.core.bukkit.command.impl.punishment.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.command.impl.punishment.IPunishmentCommand;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.command.CommandSender;

public class WarnCommand extends BaseCommand implements IPunishmentCommand {

    @Override
    public PunishmentType getPunishmentType() {
        return PunishmentType.WARN;
    }

    @CommandAlias("twarn|tempwarn")
    @CommandPermission("core.command.tempban")
    @CommandCompletion("@players @reasons")
    public void temp(CommandSender sender, @Name("player") AsyncCorePlayer player, @Name("duration") TimeDuration duration, @Name("reason {-s}") String reason) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            String actualReason = reason == null ? "None specified" : reason;

            punish(sender, uuid, actualReason, duration.transform(), false, actualReason.contains("-s"));
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

    @CommandAlias("warn")
    @CommandPermission("core.command.ban")
    @CommandCompletion("@players @reasons")
    public void perm(CommandSender sender, @Name("player") AsyncCorePlayer player, @Name("reason {-s}") String reason) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            String actualReason = reason == null ? "None specified" : reason;

            punish(sender, uuid, actualReason, Long.MAX_VALUE, false, actualReason.contains("-s"));
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

}
