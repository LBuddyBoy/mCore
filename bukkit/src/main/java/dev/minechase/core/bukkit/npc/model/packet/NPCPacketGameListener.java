package dev.minechase.core.bukkit.npc.model.packet;

import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class NPCPacketGameListener extends ServerGamePacketListenerImpl {

    public NPCPacketGameListener(MinecraftServer server, Connection connection, ServerPlayer player, CommonListenerCookie clientData) {
        super(server, connection, player, clientData);
    }

}