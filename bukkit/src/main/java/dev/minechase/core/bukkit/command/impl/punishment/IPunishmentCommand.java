package dev.minechase.core.bukkit.command.impl.punishment;

import co.aikar.commands.BaseCommand;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public interface IPunishmentCommand {

    PunishmentType getPunishmentType();

    default void punish(CommandSender sender, UUID targetUUID, String reason, long duration, boolean ipRelated, boolean shadow, boolean silent) {
        CorePlugin.getInstance().getPunishmentHandler().punish(sender, getPunishmentType(), targetUUID, reason, duration, ipRelated, shadow, silent);
    }

    default void punish(CommandSender sender, UUID targetUUID, String reason, long duration, boolean shadow, boolean silent) {
        CorePlugin.getInstance().getPunishmentHandler().punish(sender, getPunishmentType(), targetUUID, reason, duration, false, shadow, silent);
    }

    default void unpunish(CommandSender sender, UUID targetUUID, String reason, boolean silent) {
        CorePlugin.getInstance().getPunishmentHandler().unpunish(sender, getPunishmentType(), targetUUID, reason, silent);
    }

}
