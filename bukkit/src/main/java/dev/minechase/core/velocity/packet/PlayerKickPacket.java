package dev.minechase.core.velocity.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor
public class PlayerKickPacket implements Packet {

    private final UUID playerUUID;
    private final String kickMessage;

    @Override
    public void receive() {

    }

}
