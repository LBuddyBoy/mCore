package dev.minechase.core.bukkit.npc.model.packet;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;

public final class NPCConnection extends Connection {

    public NPCConnection(final PacketFlow side) {
        super(side);
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void send(@NotNull final Packet<?> packet) {
        // Do nothing
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext channelhandlercontext, final Packet<?> packet) {

    }
}