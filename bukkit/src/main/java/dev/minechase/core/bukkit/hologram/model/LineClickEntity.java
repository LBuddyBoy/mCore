package dev.minechase.core.bukkit.hologram.model;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;

public class LineEntity extends ArmorStand {

    private final HologramLine line;

    public LineEntity(Level level, HologramLine line) {
        super(EntityType.ARMOR_STAND, level);

        this.line = line;
    }

    public LineEntity(Level level, double x, double y, double z, HologramLine line) {
        super(level, x, y, z);
        this.line = line;
    }

}
