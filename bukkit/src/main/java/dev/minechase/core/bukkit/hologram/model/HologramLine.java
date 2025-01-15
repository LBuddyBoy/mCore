package dev.minechase.core.bukkit.hologram.model;

import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.placeholder.model.PlaceholderProvider;
import dev.lbuddyboy.commons.util.CC;
import lombok.Data;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class HologramLine {

    private final UUID id;
    private final IHologram parent;
    private int index;
    private String text;
    protected Map<Player, Integer> armorStandIds = new ConcurrentHashMap<>();

    public HologramLine(IHologram parent, int index, String text) {
        this.id = UUID.randomUUID();
        this.parent = parent;
        this.index = index;
        this.text = text;
    }

    public List<Packet<?>> getCreatePackets(Player player) {
        ArmorStand armorStand = this.createArmorStand(player);

        return Arrays.asList(
                new ClientboundAddEntityPacket(
                        armorStand.getId(),
                        armorStand.getUUID(),
                        armorStand.getX(),
                        armorStand.getY(),
                        armorStand.getZ(),
                        0.0f,
                        0.0f,
                        EntityType.ARMOR_STAND,
                        0,
                        Vec3.ZERO,
                        0.0D
                ),
                new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData().getNonDefaultValues())
        );
    }

    public List<Packet<?>> getUpdatePackets(Player player) {
        ArmorStand armorStand = this.createArmorStand(player);

        return Arrays.asList(
                new ClientboundTeleportEntityPacket(
                        armorStand.getId(),
                        new PositionMoveRotation(armorStand.position(), armorStand.getDeltaMovement(), armorStand.getYRot(), armorStand.getXRot()),
                        Collections.emptySet(),
                        armorStand.onGround
                ),
                new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData().getNonDefaultValues())
        );
    }

    public ClientboundRemoveEntitiesPacket getDespawnPacket(Player player) {
        ArmorStand armorStand = new ArmorStand(EntityType.ARMOR_STAND, ((CraftWorld)parent.getLocation().getWorld()).getHandle());
        armorStand.setUUID(this.id);

        if (armorStandIds.containsKey(player)) {
            armorStand.setId(armorStandIds.get(player));
        }

        armorStandIds.remove(player);

        return new ClientboundRemoveEntitiesPacket(armorStand.getId());
    }

    protected ArmorStand createArmorStand(Player player) {
        ArmorStand armorStand = new ArmorStand(EntityType.ARMOR_STAND, ((CraftWorld)parent.getLocation().getWorld()).getHandle());
        Location location = this.parent.getLocation();

        armorStand.setUUID(this.id);

        if (armorStandIds.containsKey(player)) {
            armorStand.setId(armorStandIds.get(player));
        } else {
            armorStandIds.put(player, armorStand.getId());
        }

        String text = this.text;

        for (PlaceholderProvider provider : CommonsPlugin.getInstance().getPlaceholderHandler().getPlaceholderProviders().values()) {
            text = provider.applyPlaceholders(player, text);
        }

        armorStand.setInvisible(true);
        armorStand.setCustomName((Component) Array.get(CraftChatMessage.fromString(CC.translate(text)), 0));

        String translatedText = CC.stripColor(CC.translate(text));

        armorStand.setCustomNameVisible(!translatedText.isEmpty() && !translatedText.isBlank() && !translatedText.equalsIgnoreCase(" "));
        armorStand.setPos(location.getX(), location.getY() + (index * parent.getLineOffset()), location.getZ());

        return armorStand;
    }

}