package dev.minechase.core.bukkit.listener;

import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.api.event.CoreChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class PunishmentListener implements Listener {

    @EventHandler
    public void onChat(CoreChatEvent event) {
        Player player = event.getPlayer();
        List<Punishment> punishments = event.getPunishments();

        for (Punishment punishment : punishments) {
            if (punishment.getType() != PunishmentType.MUTE) continue;

            if (punishment.isShadow()) {
                event.setShadowMute(true);
                return;
            }

            event.setCancelled(true);
            player.sendMessage(CC.translate("<blend:&4;&c>You are currently muted for " + TimeUtils.formatIntoDetailedString(punishment.getTimeLeft()) + ".</>"));
            return;
        }
    }

}
