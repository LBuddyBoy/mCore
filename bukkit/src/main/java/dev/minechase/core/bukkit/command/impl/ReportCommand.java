package dev.minechase.core.bukkit.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.report.model.Report;
import dev.minechase.core.api.report.packet.ReportUpdatePacket;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.menu.ViewGrantsMenu;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import dev.minechase.core.bukkit.packet.ReportCreatePacket;
import dev.minechase.core.bukkit.packet.StaffMessagePacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ReportCommand extends BaseCommand {

    @CommandAlias("report")
    @CommandCompletion("@players")
    public void report(Player sender, @Name("target") AsyncCorePlayer player, @Name("reason") String reason) {
        User user = CorePlugin.getInstance().getUserHandler().getUser(sender.getUniqueId());
        long cooldown = user.getPersistentMetadata().getLong("report_cooldown");

        if (cooldown > System.currentTimeMillis()) {
            sender.sendMessage(CC.translate("<blend:&4;&c>You cannot report for " + TimeUtils.formatIntoDetailedString(cooldown - System.currentTimeMillis()) + ".</>"));
            return;
        }

        player.getUUID().whenCompleteAsyncExcept(targetUUID -> {
            Report report = new Report(sender.getUniqueId(), CorePlugin.getInstance().getServerName(), reason, targetUUID);

            new ReportUpdatePacket(report).send();
            new ReportCreatePacket(report).send();
            user.getPersistentMetadata().setLong("report_cooldown", System.currentTimeMillis() + 60_000L);

            sender.sendMessage(CC.translate("<blend:&2;&a>Successfully reported " + player.getName() + "!</>"));
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

    @CommandAlias("helpop|request")
    public void helpop(Player sender, @Name("reason") String reason) {
        User user = CorePlugin.getInstance().getUserHandler().getUser(sender.getUniqueId());
        long cooldown = user.getPersistentMetadata().getLong("request_cooldown");

        if (cooldown > System.currentTimeMillis()) {
            sender.sendMessage(CC.translate("<blend:&4;&c>You cannot request for " + TimeUtils.formatIntoDetailedString(cooldown - System.currentTimeMillis()) + ".</>"));
            return;
        }

        Report report = new Report(sender.getUniqueId(), CorePlugin.getInstance().getServerName(), reason);

        new ReportUpdatePacket(report).send();
        new ReportCreatePacket(report).send();
        user.getPersistentMetadata().setLong("request_cooldown", System.currentTimeMillis() + 60_000L);

        sender.sendMessage(CC.translate("<blend:&2;&a>Successfully requested for help! Our staff will assist you shortly.</>"));
    }

    @CommandAlias("reportgo")
    @CommandPermission(CoreConstants.STAFF_PERM)
    public void reportGo(Player sender, @Name("report") String uuidString) {
        UUID id = UUID.fromString(uuidString);
        Report report = CorePlugin.getInstance().getReportHandler().getReports().get(id);

        if (report == null) return;

        if (CorePlugin.getInstance().getServerName().equalsIgnoreCase(report.getServer())) {
            Player target = Bukkit.getPlayer(report.getSenderUUID());

            if (target == null) {
                sender.sendMessage(CC.translate("&cThat player is not online."));
                return;
            }

            sender.teleport(target);
            return;
        }

        sender.chat("/server " + report.getServer());
    }


}
