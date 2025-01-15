package dev.minechase.core.bukkit.hologram.model;

import dev.minechase.core.bukkit.CorePlugin;
import lombok.Getter;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class MemoryHologram implements IHologram {

    private final String id;
    private Location location;
    private final boolean spawned;
    private final List<HologramLine> lines = new CopyOnWriteArrayList<>();
    protected Map<Integer, ArmorStand> armorStands = new HashMap<>();

    public MemoryHologram(String id, Location location, List<String> lines) {
        this.id = id;
        this.location = location;
        this.spawned = true;

        int index = 0;

        for (String text : lines) {
            this.lines.add(new HologramLine(this, index++, text));
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.spawnHologram(player);
        }

        CorePlugin.getInstance().getHologramHandler().getHolograms().put(this.id, this);
    }

    @Override
    public boolean isAutoUpdating() {
        return true;
    }

    @Override
    public long getAutoUpdateMillis() {
        return 1_000L;
    }

    @Override
    public void move(Location location) {
        this.location = location.clone();

        IHologram.super.move(location);
    }
}