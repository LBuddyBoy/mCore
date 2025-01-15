package dev.minechase.core.bukkit.npc.model;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.minechase.core.bukkit.npc.model.packet.NPCChannel;
import dev.minechase.core.bukkit.npc.model.packet.NPCConnection;
import dev.minechase.core.bukkit.npc.model.packet.NPCPacketGameListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.CraftWorld;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NPCEntity extends ServerPlayer {

    private final CustomNPC npc;

    public NPCEntity(CustomNPC npc) {
        super(((CraftWorld) npc.getSpawnLocation().getWorld()).getHandle().getServer(), ((CraftWorld) npc.getSpawnLocation().getWorld()).getHandle(), new GameProfile(npc.getUniqueId(), ""), ClientInformation.createDefault());
        this.npc = npc;

        this.updateGameProfile();
        this.setUUID(npc.getUniqueId());
        this.setRot(npc.getSpawnLocation().getYaw(), npc.getSpawnLocation().getPitch());
        this.setPos(npc.getSpawnLocation().getX(), npc.getSpawnLocation().getY(), npc.getSpawnLocation().getZ());
        this.setCustomNameVisible(false);
        this.setCustomName(null);
        this.connection = new NPCPacketGameListener(
                server,
                new NPCConnection(PacketFlow.CLIENTBOUND),
                this,
                CommonListenerCookie.createInitial(this.getGameProfile(), false)
        );

        this.connection.connection.channel = new NPCChannel();
    }

    public List<Packet<?>> getCreatePackets() {
        this.setCustomNameVisible(false);
        this.setCustomName(null);
        this.updateGameProfile();

        return Arrays.asList(
                new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this),
                new ClientboundAddEntityPacket(
                        this.getId(),
                        this.getUUID(),
                        npc.getSpawnLocation().getX(),
                        npc.getSpawnLocation().getY(),
                        npc.getSpawnLocation().getZ(),
                        npc.getSpawnLocation().getPitch(),
                        npc.getSpawnLocation().getYaw(),
                        EntityType.PLAYER,
                        0,
                        Vec3.ZERO,
                        0.0D
                ),
                new ClientboundTeleportEntityPacket(
                        this.getId(),
                        new PositionMoveRotation(this.position(), this.getDeltaMovement(), this.getYRot(), this.getXRot()),
                        Collections.emptySet(),
                        this.onGround
                ),
                new ClientboundRotateHeadPacket(this, Mth.packDegrees(this.getYHeadRot())),
                new ClientboundSetEntityDataPacket(this.getId(), this.getEntityData().getNonDefaultValues())
        );
    }

    public List<Packet<?>> getUpdatePackets() {
        this.setCustomNameVisible(false);
        this.setCustomName(null);
        this.setPos(npc.getSpawnLocation().getX(), npc.getSpawnLocation().getY(), npc.getSpawnLocation().getZ());
        this.setRot(npc.getSpawnLocation().getYaw(), npc.getSpawnLocation().getPitch());
        this.setYHeadRot(this.getYRot());
        this.setYBodyRot(this.getXRot());
        this.updateGameProfile();

        return Arrays.asList(
                new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this),
                new ClientboundTeleportEntityPacket(
                        this.getId(),
                        new PositionMoveRotation(this.position(), this.getDeltaMovement(), this.getYRot(), this.getXRot()),
                        Collections.emptySet(),
                        this.onGround
                ),
                new ClientboundRotateHeadPacket(this, Mth.packDegrees(this.getYHeadRot())),
                new ClientboundSetEntityDataPacket(this.getId(), this.getEntityData().getNonDefaultValues())
        );
    }

    public List<Packet<?>> getRemovePackets() {
        return Arrays.asList(
                new ClientboundRemoveEntitiesPacket(this.getId())
        );
    }

    private void updateGameProfile() {
        gameProfile.getProperties().removeAll("textures");

        gameProfile.getProperties().put("textures", new Property(
                "textures",
                npc.getSkinTexture(),
                npc.getSkinSignature()
        ));
    }

}