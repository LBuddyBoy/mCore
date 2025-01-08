package dev.minechase.core.bukkit.listener;

import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.model.PunishmentSnapshot;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.bukkit.api.event.AsyncCoreChatEvent;
import dev.minechase.core.bukkit.api.event.AsyncCoreLoginEvent;
import dev.minechase.core.bukkit.packet.StaffMessagePacket;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class PunishmentListener implements Listener {

    @EventHandler
    public void onChat(AsyncCoreChatEvent event) {
        Player player = event.getPlayer();
        List<PunishmentSnapshot> punishments = event.getPunishments();

        for (PunishmentSnapshot snapshot : punishments) {
            Punishment punishment = snapshot.punishment();
            if (punishment.getType() != PunishmentType.MUTE) continue;
            if (!punishment.isIpRelated() && !punishment.getTargetUUID().equals(player.getUniqueId())) continue;

            if (punishment.isShadow()) {
                event.setShadowMute(true);
                return;
            }

            event.setCancelled(true);
            player.sendMessage(CC.translate("<blend:&4;&c>You are currently muted for " + punishment.getDurationString() + ".</>"));
            new StaffMessagePacket(CC.translate(
                    "<blend:&4;&c>" + player.getName() + " tried to speak, but is muted.</> &7(" + punishment.getReason() + ")"
            )).send();
            return;
        }
    }

    @EventHandler
    public void onAsyncPreLogin(AsyncCoreLoginEvent event) {
        for (PunishmentSnapshot snapshot : event.getPunishments()) {
            Punishment punishment = snapshot.punishment();

            if (punishment.getType() == PunishmentType.MUTE || punishment.getType() == PunishmentType.KICK || punishment.getType() == PunishmentType.WARN) continue;
            if (!punishment.isActive()) continue;

            event.setKickMessage(CC.translate(punishment.getKickMessage()));
            event.setCancelled(true);
            new StaffMessagePacket(CC.translate(
                    "<blend:&4;&c>" + event.getName() + " tried to join, but is " + punishment.getType().getPlural() + ".</> &7(" + punishment.getReason() + ")"
            )).send();
            return;
        }
    }

}
