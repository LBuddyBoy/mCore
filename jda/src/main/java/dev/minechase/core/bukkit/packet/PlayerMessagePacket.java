package dev.minechase.core.bukkit.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerMessagePacket implements Packet {

    private final UUID playerUUID;
    private final List<String> messages = new ArrayList<>();

    public PlayerMessagePacket(String message, UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.messages.add(message);
    }

    public PlayerMessagePacket(List<String> messages, UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.messages.addAll(messages);
    }

    @Override
    public void receive() {

    }

}
