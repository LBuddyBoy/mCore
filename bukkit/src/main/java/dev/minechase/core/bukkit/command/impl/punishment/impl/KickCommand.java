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

public class KickCommand extends BaseCommand implements IPunishmentCommand {

    @Override
    public PunishmentType getPunishmentType() {
        return PunishmentType.KICK;
    }

    @CommandAlias("kick")
    @CommandPermission("core.command.kick")
    @CommandCompletion("@players @reasons")
    public void perm(CommandSender sender, @Name("player") AsyncCorePlayer player, @Name("reason {-s}") String reason) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            String actualReason = reason == null ? "None specified" : reason;

            punish(sender, uuid, actualReason, Long.MAX_VALUE, false, actualReason.contains("-s"));
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

}
