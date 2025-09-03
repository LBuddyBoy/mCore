package dev.minechase.core.bukkit.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class StaffMessagePacket implements Packet {

    private final List<String> messages = new ArrayList<>();

    public StaffMessagePacket(String message) {
        this.messages.add(message);
    }

    public StaffMessagePacket(List<String> messages) {
        this.messages.addAll(messages);
    }

    @Override
    public void receive() {
    }

}
