package dev.minechase.core.bukkit.hologram.model;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.util.CraftChatMessage;

import java.lang.reflect.Array;

public class LineClickEntity extends Tadpole {

    public LineClickEntity(Level level, HologramLine line) {
        super(EntityType.TADPOLE, level);

        Location location = line.getParent().getLocation();

        this.setUUID(line.getIds()[1]);
        this.setId(line.getArmorStandIds()[1]);
        this.setInvisible(true);
        this.setCustomNameVisible(false);
        this.setNoGravity(true);
        this.setPos(location.getX(), location.getY() + (((line.getIndex() + 1) * line.getParent().getLineOffset())), location.getZ());
    }

}
