package dev.minechase.core.velocity.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.util.CC;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;

import java.util.UUID;

@AllArgsConstructor
public class PlayerSendToServerPacket implements Packet {

    private final UUID playerUUID;
    private final String serverName;

    @Override
    public void receive() {
        CoreVelocity.getInstance().getServerHandler().sendPlayerToServer(this.playerUUID, this.serverName);
    }

}
