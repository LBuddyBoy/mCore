package dev.minechase.core.velocity.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class PlayerSendToServerPacket implements Packet {

    private final UUID playerUUID;
    private final String serverName;

    @Override
    public void receive() {

    }

}
