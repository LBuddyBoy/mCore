package dev.minechase.core.api.permission.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.minechase.core.api.CoreAPI;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class PlayerUpdatePermissionPacket implements Packet {

    private final UUID playerUUID;

    @Override
    public void receive() {
        CoreAPI.getInstance().getPermissionHandler().updatePermissions(this.playerUUID);
    }

}
