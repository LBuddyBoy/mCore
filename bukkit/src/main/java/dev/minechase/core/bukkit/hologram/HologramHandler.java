package dev.minechase.core.bukkit.hologram;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.packet.event.PacketReceiveEvent;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.api.event.PlayerInteractHologramEvent;
import dev.minechase.core.bukkit.api.event.PlayerInteractHologramLineEvent;
import dev.minechase.core.bukkit.hologram.model.HologramLine;
import dev.minechase.core.bukkit.hologram.model.IHologram;
import dev.minechase.core.bukkit.hologram.model.SerializableHologram;
import lombok.Getter;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class HologramHandler implements IModule, Listener {

    private final Map<String, IHologram> holograms;
    private final File directory;
    private final Map<UUID, Long> CLICK_COOLDOWN = new HashMap<>();

    public HologramHandler() {
        this.holograms = new ConcurrentHashMap<>();
        this.directory = new File(CorePlugin.getInstance().getDataFolder(), "holograms");

        if (!this.directory.exists()) this.directory.mkdir();
    }

    @Override
    public void load() {
        CorePlugin.getInstance().getServer().getPluginManager().registerEvents(this, CorePlugin.getInstance());

        for (String fileName : this.directory.list()) {
            fileName = fileName.replaceAll(".yml", "");
            Config config = new Config(CorePlugin.getInstance(), fileName, this.directory);

            this.holograms.put(fileName, new SerializableHologram(config));
        }

        Tasks.runAsyncTimer(() -> {
            this.holograms.values().stream().filter(IHologram::isAutoUpdating).forEach(IHologram::updateHologram);
        }, 20, 20);
    }

    @Override
    public void unload() {

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Tasks.runAsync(() -> {
            for (IHologram hologram : this.holograms.values()) {
                if (!hologram.getLocation().getWorld().equals(player.getWorld())) continue;


                hologram.spawnHologram(player);
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Tasks.runAsync(() -> {
            for (IHologram hologram : this.holograms.values()) {
                if (!hologram.getLocation().getWorld().equals(player.getWorld())) continue;

                hologram.despawnHologram(player);
            }
        });
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        World fromWorld = event.getFrom();
        World toWorld = event.getPlayer().getWorld();

        Tasks.runAsync(() -> {
            for (IHologram hologram : this.holograms.values()) {
                if (!hologram.getLocation().getWorld().equals(fromWorld)) {
                    if (!hologram.getLocation().getWorld().equals(toWorld)) continue;

                    hologram.spawnHologram(event.getPlayer());
                    continue;
                }

                hologram.despawnHologram(event.getPlayer());
            }
        });
    }

    @EventHandler
    public void onPacket(PacketReceiveEvent event) {
        if (!(event.getPacket() instanceof ServerboundInteractPacket packet)) return;
        if (packet.isUsingSecondaryAction()) return;

        Player player = event.getPlayer();

        long cooldown = CLICK_COOLDOWN.getOrDefault(player.getUniqueId(), 0L);

        if (cooldown > System.currentTimeMillis()) {
            return;
        }

        boolean foundLine = false;

        for (IHologram hologram : CorePlugin.getInstance().getHologramHandler().getHolograms().values()) {
            List<HologramLine> lines = hologram.getLines();

            if (foundLine) break;
            if (lines.isEmpty()) continue;

            for (HologramLine line : lines) {
                if (line.getArmorStandIds()[0] == packet.getEntityId()) {
                    PlayerInteractHologramEvent hologramEvent = new PlayerInteractHologramEvent(
                            player,
                            hologram,
                            packet.isAttack()
                    );

                    Bukkit.getPluginManager().callEvent(hologramEvent);
                    CLICK_COOLDOWN.put(player.getUniqueId(), System.currentTimeMillis() + 50L);
                }

                if (line.getArmorStandIds()[1] == packet.getEntityId()) {
                    PlayerInteractHologramLineEvent hologramEvent = new PlayerInteractHologramLineEvent(
                            player,
                            hologram,
                            line,
                            packet.isAttack()
                    );

                    Bukkit.getPluginManager().callEvent(hologramEvent);
                    foundLine = true;
                    CLICK_COOLDOWN.put(player.getUniqueId(), System.currentTimeMillis() + 50L);
                    break;
                }


            }
        }
    }

}