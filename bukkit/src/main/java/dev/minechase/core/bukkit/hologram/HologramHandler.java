package dev.minechase.core.bukkit.hologram;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.command.context.HologramContext;
import dev.minechase.core.bukkit.command.impl.HologramCommand;
import dev.minechase.core.bukkit.hologram.model.HologramLine;
import dev.minechase.core.bukkit.hologram.model.IHologram;
import dev.minechase.core.bukkit.hologram.model.SerializableHologram;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

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
        if (!CorePlugin.getInstance().getConfig().getBoolean("holograms", false)) return;

        CorePlugin.getInstance().getServer().getPluginManager().registerEvents(this, CorePlugin.getInstance());

        new HologramContext().register(CorePlugin.getInstance().getCommandHandler().getCommandManager());
        CorePlugin.getInstance().getCommandHandler().getCommandManager().getCommandCompletions().registerCompletion("hologramLines", (ctx) -> {
            IHologram hologram = ctx.getContextValue(IHologram.class);

            return hologram.getLines().stream().map(HologramLine::getIndex).map(String::valueOf).toList();
        });

        CorePlugin.getInstance().getCommandHandler().getCommandManager().registerCommand(new HologramCommand());

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