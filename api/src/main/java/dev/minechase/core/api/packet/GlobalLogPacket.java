package dev.minechase.core.api.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.minechase.core.api.CoreAPI;

import java.util.ArrayList;
import java.util.List;

public class GlobalLogPacket implements Packet {

    private final List<String> messages = new ArrayList<>();

    public GlobalLogPacket(String message) {
        this.messages.add(message);
    }

    public GlobalLogPacket(List<String> messages) {
        this.messages.addAll(messages);
    }


    @Override
    public void receive() {
        this.messages.forEach(s -> {
            CoreAPI.getInstance().getLogger().info(s);
        });
    }
}
