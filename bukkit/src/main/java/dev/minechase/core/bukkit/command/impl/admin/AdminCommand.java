package dev.minechase.core.bukkit.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.api.punishment.packet.PunishmentUpdatePacket;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.packet.StaffMessagePacket;
import dev.minechase.core.bukkit.settings.model.impl.AdminChatSetting;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AdminCommand extends BaseCommand {

    @CommandAlias("adminchat|ac")
    @CommandPermission("core.command.adminchat")
    public void adminChat(Player sender, @Name("message") @Optional String message) {
        if (message == null) {
            CorePlugin.getInstance().getSettingsHandler().getSetting(AdminChatSetting.class).toggle(sender.getUniqueId());
            return;
        }

        User user = CorePlugin.getInstance().getUserHandler().getUser(sender.getUniqueId());

        new StaffMessagePacket(CC.translate("&c[Admin Chat] " + user.getDisplayName() + "&7: &f") + message).send();
    }

    @CommandAlias("staffrollback")
    @CommandPermission("core.command.staffrollback")
    @CommandCompletion("@players @durations @punishmentTypes")
    public void staffrollback(CommandSender sender, @Name("player") UUID senderUUID, @Name("duration") TimeDuration duration, @Name("type") PunishmentType type) {
        if (sender instanceof Player) {
            return;
        }

        long startedAt = System.currentTimeMillis();

        sender.sendMessage(CC.translate("&aExecuting staff rollback..."));

        CorePlugin.getInstance().getPunishmentHandler().fetchAllPunishments().whenCompleteAsync(((punishments, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            punishments = punishments
                    .stream()
                    .filter(punishment -> punishment.isActive() && punishment.getType() == type && (punishment.getSenderUUID() != null && punishment.getSenderUUID().equals(senderUUID)) && (System.currentTimeMillis() - punishment.getSentAt() <= duration.transform()))
                    .toList();

            for (Punishment punishment : punishments) {
                punishment.remove(null, "Staff Rollback");

                new PunishmentUpdatePacket(punishment).send();
            }

            sender.sendMessage(CC.translate("&aFinished rolling back " + punishments.size() + " punishments in " + (System.currentTimeMillis() - startedAt) + " ms"));
        }));
    }


}
