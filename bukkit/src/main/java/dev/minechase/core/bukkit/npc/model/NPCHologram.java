package dev.minechase.core.bukkit.npc.model.packet;

import dev.minechase.core.bukkit.hologram.model.MemoryHologram;
import dev.minechase.core.bukkit.npc.model.INPC;
import org.bukkit.Location;

public class NPCHologram extends MemoryHologram {

    public static double HOLOGRAM_OFFSET = 0.25D;

    public NPCHologram(INPC npc) {
        super("npc_" + npc.getName(),
                new Location(
                        npc.getSpawnLocation().getWorld(),
                        npc.getSpawnLocation().getX(),
                        npc.getSpawnLocation().getY() - HOLOGRAM_OFFSET,
                        npc.getSpawnLocation().getZ()
                ),
                npc.getHologramLines());
    }

}