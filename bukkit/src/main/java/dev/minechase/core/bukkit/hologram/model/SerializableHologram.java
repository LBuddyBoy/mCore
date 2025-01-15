package dev.minechase.core.bukkit.hologram.model;

import dev.lbuddyboy.commons.util.Config;
import dev.lbuddyboy.commons.util.Coordinate;
import dev.minechase.core.bukkit.CorePlugin;
import lombok.Getter;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class SerializableHologram implements IHologram {

    private final Config config;
    private final String id;
    private String world;
    private Coordinate coordinate;
    private final List<HologramLine> lines = new CopyOnWriteArrayList<>();
    private Location location = null;
    protected Map<Integer, ArmorStand> armorStands = new HashMap<>();

    public SerializableHologram(String id, Location location, List<String> lines) {
        this.config = new Config(CorePlugin.getInstance(), id, CorePlugin.getInstance().getHologramHandler().getDirectory());
        this.id = id;
        this.world = location.getWorld().getName();
        this.coordinate = new Coordinate(location);

        int index = 0;

        for (String text : lines) {
            this.lines.add(new HologramLine(this, index++, text));
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.spawnHologram(player);
        }

        CorePlugin.getInstance().getHologramHandler().getHolograms().put(this.id, this);
        save();
    }

    public SerializableHologram(Config config) {
        this.config = config;
        this.id = config.getFileName().replaceAll(".yml", "");
        this.world = config.getString("world");
        this.coordinate = new Coordinate(config.getString("location"));

        int index = 0;
        for (String text : this.config.getStringList("lines")) {
            lines.add(new HologramLine(this, index++, text));
        }
    }

    @Override
    public void save() {
        this.config.set("world", this.world);
        this.config.set("location", this.coordinate.toString());
        this.config.set("lines", this.lines.stream().map(HologramLine::getText).toList());
        this.config.save();
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
    public Location getLocation() {
        if (location == null) location = this.coordinate.toLocation(Bukkit.getWorld(this.world));

        return location;
    }

    @Override
    public void delete() {
        IHologram.super.delete();

        if (this.config.getFile().exists()) this.config.getFile().delete();
    }

    @Override
    public void move(Location location) {
        this.world = location.getWorld().getName();
        this.coordinate = new Coordinate(location);
        this.location = location.clone();

        IHologram.super.move(location);
    }
}