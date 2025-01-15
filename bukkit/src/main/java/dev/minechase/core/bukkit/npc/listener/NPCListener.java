package dev.minechase.core.bukkit.npc.listener;

import dev.lbuddyboy.commons.packet.event.PacketReceiveEvent;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.npc.model.CustomNPC;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.entity.Entity;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPCListener implements Listener {

    private final Map<UUID, Long> CLICK_COOLDOWN = new HashMap<>();

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        for (CustomNPC npc : CorePlugin.getInstance().getNpcHandler().getNpcs().values()) {
            if (!npc.getWorld().equalsIgnoreCase(event.getWorld().getName())) continue;

            npc.spawnNPC();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Tasks.runAsync(() -> {
            for (CustomNPC npc : CorePlugin.getInstance().getNpcHandler().getNpcs().values()) {
                if (!npc.getWorld().equalsIgnoreCase(player.getWorld().getName())) continue;

                npc.showNPC(player);
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Tasks.runAsync(() -> {
            for (CustomNPC npc : CorePlugin.getInstance().getNpcHandler().getNpcs().values()) {
                if (!npc.getWorld().equalsIgnoreCase(player.getWorld().getName())) continue;

                npc.hideNPC(player);
            }
        });

        CLICK_COOLDOWN.remove(player.getUniqueId());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        World fromWorld = event.getFrom();
        World toWorld = event.getPlayer().getWorld();

        Tasks.runAsync(() -> {
            for (CustomNPC npc : CorePlugin.getInstance().getNpcHandler().getNpcs().values()) {
                if (!npc.getWorld().equals(fromWorld.getName())) {
                    if (!npc.getWorld().equals(toWorld.getName())) continue;

                    npc.showNPC(event.getPlayer());
                    continue;
                }

                npc.hideNPC(event.getPlayer());
            }
        });
    }

    @EventHandler
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!(event.getPacket() instanceof ServerboundInteractPacket packet)) return;
        if (packet.isAttack()) return;
        if (packet.isUsingSecondaryAction()) return;

        Player player = event.getPlayer();
        CustomNPC npc = CorePlugin.getInstance().getNpcHandler().getNpcById().getOrDefault(packet.getEntityId(), null);

        if (npc == null) return;
        if (npc.getRightClickCommand() == null || npc.getRightClickCommand().isEmpty()) return;

        long cooldown = CLICK_COOLDOWN.getOrDefault(player.getUniqueId(), 0L);

        if (cooldown > System.currentTimeMillis()) {
            return;
        }

        Tasks.run(() -> player.chat("/" + npc.getRightClickCommand().replaceAll("%player%", player.getName())));

        CLICK_COOLDOWN.put(player.getUniqueId(), System.currentTimeMillis() + 3_000L);
    }

}