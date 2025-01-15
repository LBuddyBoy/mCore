package dev.minechase.core.bukkit.hologram;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.hologram.model.IHologram;
import dev.minechase.core.bukkit.hologram.model.SerializableHologram;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class HologramHandler implements IModule, Listener {

    private final Map<String, IHologram> holograms;
    private final File directory;

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

}