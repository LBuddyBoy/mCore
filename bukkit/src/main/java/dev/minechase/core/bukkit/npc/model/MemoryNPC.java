package dev.minechase.core.bukkit.npc.model;

import com.google.common.base.Preconditions;
import dev.lbuddyboy.commons.util.Coordinate;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.bukkit.CorePlugin;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static dev.minechase.core.bukkit.npc.model.NPCHologram.HOLOGRAM_OFFSET;

@Getter
public abstract class MemoryNPC implements INPC {

    private final UUID uniqueId;
    private final String name;
    private String skinTexture, skinSignature;
    private String world;
    private Coordinate coordinate;
    private String rightClickCommand = "";

    private Location spawnLocation;

    protected NPCHologram hologram;
    protected NPCEntity entity;
    protected boolean spawned = false;

    public MemoryNPC(String name, Location spawnLocation) {
        this.uniqueId = UUID.randomUUID();
        this.name = name;
        this.skinTexture = "eyJ0aW1lc3RhbXAiOjE1ODc4MjU0NzgwNDcsInByb2ZpbGVJZCI6ImUzYjQ0NWM4NDdmNTQ4ZmI4YzhmYTNmMWY3ZWZiYThlIiwicHJvZmlsZU5hbWUiOiJNaW5pRGlnZ2VyVGVzdCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2E1ODg4YWEyZDdlMTk5MTczYmEzN2NhNzVjNjhkZTdkN2Y4NjJiMzRhMTNiZTMyNDViZTQ0N2UyZjIyYjI3ZSJ9fX0=";
        this.skinSignature = "Yt6VmTAUTbpfGQoFneECtoYcbu7jcARAwZu2LYWv3Yf1MJGXv6Bi3i7Pl9P8y1ShB7V1Q2HyA1bce502x1naOKJPzzMJ0jKZfEAKXnzaFop9t9hXzgOq7PaIAM6fsapymYhkkulRIxnJdMrMb2PLRYfo9qiBJG+IEbdj8MTSvWJO10xm7GtpSMmA2Xd0vg5205hsj0OxSdgxf1uuWPyRaXpPZYDUU05/faRixDKti86hlkBs/v0rttU65r1UghkftfjK0sJoPpk9hABvkw4OjXVFb63wcb27KPhIiSHZzTooSxjGNDniauCsF8Je+fhhMebpXeba1R2lZPLhkHwazNgZmTCKbV1M/a8BDHN24HH9okJpQOR9SPCPOJrNbK+LTPsrR06agj+H/yvYq0ZMJTF6IE6C3KJqntPJF1NQvJM0/YegPPtzpbT/7O1cd4JBCVmguhadOFYvrxqCKHcmaYdkyMJtnGub/5sCjJAG7fZadACftwLnmdBZoQRcNKQMubpdUjuzF8g6C03MiZkeNBUgqkfVjXi7DqpmB0ZvTttp34vy7EIBCo3Hfj15779nGs8SoTw9V2zZc+LgiVPjWF6tffjWkgzLq8K2Cndu6RDlWGJWmrztN/X9lIiLdn8GEfSSGY983n0C91x8mkpOKSfAWPnSZd7NuHU5GaoMvyE=";
        this.world = spawnLocation.getWorld().getName();
        this.coordinate = new Coordinate(spawnLocation);

        CorePlugin.getInstance().getNpcHandler().getNpcs().put(name, this);

        this.spawnNPC();
    }

    public Entity getNMSEntity() {
        return this.entity;
    }

    public void teleport(Location location) {
        this.world = location.getWorld().getName();
        this.coordinate = new Coordinate(location);
        this.spawnLocation = location.clone();
        this.hologram.move(new Location(
                location.getWorld(),
                location.getX(),
                location.getY() - HOLOGRAM_OFFSET,
                location.getZ()
        ));

        this.updateNPC();
    }

    @Override
    public int getId() {
        return this.entity.getId();
    }

    @SneakyThrows
    @Override
    public void spawnNPC() {
        if (spawned) return;

        Preconditions.checkNotNull(this.getSpawnLocation().getWorld(), "Couldn't spawn NPC, couldn't find spawnLocation world.");

        this.spawned = true;
        this.entity = new NPCEntity(this);
        this.hologram = new NPCHologram(this);

        this.showNPC();

        CorePlugin.getInstance().getNpcHandler().getNpcById().put(this.entity.getId(), this);
    }

    @Override
    public void showNPC(Player viewer) {
        this.entity.getCreatePackets(viewer).forEach(packet -> ((CraftPlayer)viewer).getHandle().connection.send(packet));
        this.entity.getUpdatePackets(viewer).forEach(packet -> ((CraftPlayer)viewer).getHandle().connection.send(packet));
    }

    @Override
    public void hideNPC(Player viewer) {
        this.entity.getRemovePackets().forEach(packet -> ((CraftPlayer)viewer).getHandle().connection.send(packet));
    }

    @Override
    public List<Player> getViewers() {
        return this.getSpawnLocation().getWorld().getPlayers();
    }

    public void setSkin(String skinTexture, String skinSignature) {
        this.skinTexture = skinTexture;
        this.skinSignature = skinSignature;

        this.hideNPC();

        Tasks.runAsyncLater(() -> {
            this.showNPC();
        }, 5);
    }

    @Override
    public void updateNPC(Player viewer) {
        this.entity.getUpdatePackets(viewer).forEach(packet -> ((CraftPlayer)viewer).getHandle().connection.send(packet));
    }

    @Override
    public void despawnNPC() {
        this.spawned = false;

        for (Player player : this.getSpawnLocation().getWorld().getPlayers()) {
            this.entity.getRemovePackets().forEach(packet -> ((CraftPlayer)player).getHandle().connection.send(packet));
        }

        this.hologram.despawnHologram();
        this.entity.remove(Entity.RemovalReason.DISCARDED);

        this.entity = null;
        this.hologram = null;
    }

    @Override
    public Location getSpawnLocation() {
        if (spawnLocation == null) spawnLocation = this.coordinate.toLocation(Bukkit.getWorld(this.world));

        return spawnLocation;
    }


}