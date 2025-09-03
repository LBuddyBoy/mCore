package dev.minechase.core.bukkit.npc.model;

import dev.minechase.core.bukkit.hologram.model.MemoryHologram;
import org.bukkit.Location;

public class NPCHologram extends MemoryHologram {

    public static double HOLOGRAM_OFFSET = 0.25D;
    protected INPC npc;

    public NPCHologram(INPC npc) {
        super("npc_" + npc.getName(),
                new Location(
                        npc.getSpawnLocation().getWorld(),
                        npc.getSpawnLocation().getX(),
                        npc.getSpawnLocation().getY() - HOLOGRAM_OFFSET,
                        npc.getSpawnLocation().getZ()
                ),
                npc.getHologramLines());

        this.npc = npc;
    }

    @Override
    public void save() {
        if (!(this.npc instanceof CustomNPC customNPC)) {
            return;
        }

        customNPC.save();
    }
}