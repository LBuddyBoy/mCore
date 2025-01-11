package dev.minechase.core.bukkit.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.lbuddyboy.commons.component.FancyBuilder;
import dev.minechase.core.api.report.model.Report;
import dev.minechase.core.bukkit.CoreConstants;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ReportCreatePacket implements Packet {

    private final Report report;

    @Override
    public void receive() {
        FancyBuilder headerBuilder = this.report.isReport() ?
                new FancyBuilder("&4[Reports] &6" + this.report.getSenderName() + "&e reported &6" + this.report.getTargetName() + "&e for &7'" + this.report.getReason() + "'") :
                new FancyBuilder("&4[Requests] &6" + this.report.getSenderName() + "&e requested for help &7'" + this.report.getReason() + "'");
        FancyBuilder helpComponent = new FancyBuilder("&a[Teleport]");

        helpComponent.click(ClickEvent.Action.RUN_COMMAND, "/reportgo " + this.report.getId().toString());

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission(CoreConstants.STAFF_PERM)) continue;

            player.sendMessage(" ");
            headerBuilder.send(player);
            helpComponent.send(player);
            player.sendMessage(" ");
        }

        Bukkit.getConsoleSender().sendMessage(" ");
        headerBuilder.send(Bukkit.getConsoleSender());
        helpComponent.send(Bukkit.getConsoleSender());
        Bukkit.getConsoleSender().sendMessage(" ");
    }

}
