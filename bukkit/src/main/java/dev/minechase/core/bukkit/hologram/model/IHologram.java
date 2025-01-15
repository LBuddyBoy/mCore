package dev.minechase.core.bukkit.hologram.model;

import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.bukkit.CorePlugin;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IHologram {

    String getId();
    Location getLocation();
    List<HologramLine> getLines();
    boolean isAutoUpdating();
    long getAutoUpdateMillis();

    default double getLineOffset() {
        return 0.25D;
    }

    default boolean removeLine(int index) {
        List<HologramLine> lines = this.getLines();

        if (index < 0 || index >= lines.size()) {

            return false;
        }

        HologramLine toRemove = lines.get(index);
        for (Player viewer : getLocation().getWorld().getPlayers()) {
            ((CraftPlayer)viewer).getHandle().connection.sendPacket(toRemove.getDespawnPacket(viewer));
        }

        lines.remove(toRemove);

        int newIndex = 0;

        for (HologramLine line : this.getLines()) {
            line.setIndex(newIndex++);
        }

        this.updateHologram();
        this.save();

        return true;
    }

    default boolean setLine(int index, String text) {
        List<HologramLine> lines = this.getLines();

        if (index < 0 || index >= lines.size()) {

            return false;
        }

        lines.get(index).setText(text);
        this.updateHologram();
        this.save();
        return true;
    }

    default void addLine(String text) {
        List<HologramLine> lines = this.getLines();
        HologramLine line = new HologramLine(this, lines.size(), text);

        lines.add(line);
        this.updateHologram();

        for (Player player : getLocation().getWorld().getPlayers()) {
            line.getCreatePackets(player).forEach(packet -> ((CraftPlayer)player).getHandle().connection.sendPacket(packet));
        }

        this.save();
    }

    default void addLines(List<String> lines) {
        lines.forEach(this::addLine);
    }

    default void updateHologram() {
        for (Player viewer : getLocation().getWorld().getPlayers()) {
            for (HologramLine line : this.getLines()) {
                line.getUpdatePackets(viewer).forEach(packet -> ((CraftPlayer)viewer).getHandle().connection.sendPacket(packet));
            }
        }
    }

    default void spawnHologram(Player viewer) {
        for (HologramLine line : this.getLines()) {
            line.getCreatePackets(viewer).forEach(packet -> ((CraftPlayer)viewer).getHandle().connection.sendPacket(packet));
        }
    }

    default void spawnHologram() {
        Bukkit.getOnlinePlayers().forEach(this::spawnHologram);
    }

    default void despawnHologram(Player viewer) {
        for (HologramLine line : this.getLines()) {
            ((CraftPlayer)viewer).getHandle().connection.sendPacket(line.getDespawnPacket(viewer));
        }
    }

    default void despawnHologram() {
        Bukkit.getOnlinePlayers().forEach(this::despawnHologram);
    }

    default void save() {

    }

    default void move(Location location) {
        this.despawnHologram();
        this.spawnHologram();

        save();
    }

    default void delete() {
        for (Player viewer : getLocation().getWorld().getPlayers()) {
            this.despawnHologram(viewer);
        }

        CorePlugin.getInstance().getHologramHandler().getHolograms().remove(this.getId());
    }

}