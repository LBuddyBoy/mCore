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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.*;

@Data
public class HologramLine {

    private final UUID[] ids = new UUID[2];
    private final IHologram parent;
    private int index;
    private String text;

    protected int[] armorStandIds = new int[2];

    public HologramLine(IHologram parent, int index, String text) {
        this.ids[0] = UUID.randomUUID(); // display line uuid
        this.ids[1] = UUID.randomUUID(); // clickable line uuid
        this.parent = parent;
        this.index = index;
        this.text = text;
        this.armorStandIds[0] = Entity.nextEntityId(); // line id
        this.armorStandIds[1] = Entity.nextEntityId(); // clickable line id
    }

    public List<Packet<?>> getCreatePackets(Player player) {
        LineEntity armorStand = this.createLineEntity(player);
        LineClickEntity clickEntity = this.createClickEntity();

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
                new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData().getNonDefaultValues()),
                new ClientboundAddEntityPacket(
                        clickEntity.getId(),
                        clickEntity.getUUID(),
                        clickEntity.getX(),
                        clickEntity.getY(),
                        clickEntity.getZ(),
                        0.0f,
                        0.0f,
                        EntityType.TADPOLE,
                        0,
                        Vec3.ZERO,
                        0.0D
                ),
                new ClientboundSetEntityDataPacket(clickEntity.getId(), clickEntity.getEntityData().getNonDefaultValues())
        );
    }

    public List<Packet<?>> getUpdatePackets(Player player) {
        LineEntity armorStand = this.createLineEntity(player);
        LineClickEntity clickEntity = this.createClickEntity();

        return Arrays.asList(
                new ClientboundTeleportEntityPacket(
                        armorStand.getId(),
                        new PositionMoveRotation(armorStand.position(), armorStand.getDeltaMovement(), armorStand.getYRot(), armorStand.getXRot()),
                        Collections.emptySet(),
                        armorStand.onGround
                ),
                new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData().getNonDefaultValues()),

                new ClientboundTeleportEntityPacket(
                        clickEntity.getId(),
                        new PositionMoveRotation(clickEntity.position(), clickEntity.getDeltaMovement(), clickEntity.getYRot(), clickEntity.getXRot()),
                        Collections.emptySet(),
                        clickEntity.onGround
                ),
                new ClientboundSetEntityDataPacket(clickEntity.getId(), clickEntity.getEntityData().getNonDefaultValues())
        );
    }

    public List<ClientboundRemoveEntitiesPacket> getDespawnPackets() {
        LineEntity armorStand = new LineEntity(((CraftWorld) parent.getLocation().getWorld()).getHandle(), this);
        LineClickEntity clickEntity = new LineClickEntity(((CraftWorld) parent.getLocation().getWorld()).getHandle(), this);

        armorStand.setUUID(this.ids[0]);
        armorStand.setId(this.armorStandIds[0]);
        clickEntity.setUUID(this.ids[1]);
        clickEntity.setId(this.armorStandIds[1]);

        return Arrays.asList(
                new ClientboundRemoveEntitiesPacket(armorStand.getId()),
                new ClientboundRemoveEntitiesPacket(clickEntity.getId())
        );
    }

    protected LineEntity createLineEntity(Player player) {
        LineEntity armorStand = new LineEntity(((CraftWorld) parent.getLocation().getWorld()).getHandle(), this);
        Location location = this.parent.getLocation();

        armorStand.setUUID(this.ids[0]);
        armorStand.setId(this.armorStandIds[0]);

        String text = this.text;

        for (PlaceholderProvider provider : CommonsPlugin.getInstance().getPlaceholderHandler().getPlaceholderProviders().values()) {
            text = provider.applyPlaceholders(player, text);
        }

        armorStand.setInvisible(true);
        armorStand.setCustomName((Component) Array.get(CraftChatMessage.fromString(CC.translate(text)), 0));
        armorStand.setSilent(true);

        String translatedText = CC.stripColor(CC.translate(text));

        armorStand.setCustomNameVisible(!translatedText.isEmpty() && !translatedText.isBlank() && !translatedText.equalsIgnoreCase(" "));
        armorStand.setPos(location.getX(), location.getY() + (index * parent.getLineOffset()), location.getZ());

        return armorStand;
    }

    protected LineClickEntity createClickEntity() {
        return new LineClickEntity(((CraftWorld) parent.getLocation().getWorld()).getHandle(), this);
    }

}