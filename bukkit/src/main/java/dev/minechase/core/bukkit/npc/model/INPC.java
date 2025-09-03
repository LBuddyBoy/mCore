package dev.minechase.core.bukkit.npc.model;

import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface INPC {

    String getName();
    UUID getUniqueId();
    int getId();
    String getWorld();
    Location getSpawnLocation();
    String getSkinTexture();
    String getSkinSignature();
    String getRightClickCommand();
    List<String> getHologramLines();
    void spawnNPC();
    void despawnNPC();
    void updateNPC(Player viewer);
    void showNPC(Player viewer);
    void hideNPC(Player viewer);
    NPCHologram getHologram();
    List<Player> getViewers();

    default void onEntityCreate(NPCEntity entity) {

    }

    default void delete() {
        CorePlugin.getInstance().getNpcHandler().getNpcs().remove(this.getName());
        CorePlugin.getInstance().getNpcHandler().getNpcById().remove(this.getId());

        this.despawnNPC();
        this.getHologram().delete();
    }

    default void updateNPC() {
        this.getViewers().forEach(this::updateNPC);
    }

    default void showNPC() {
        this.getViewers().forEach(this::showNPC);
    }

    default void hideNPC() {
        this.getViewers().forEach(this::hideNPC);
    }

}
