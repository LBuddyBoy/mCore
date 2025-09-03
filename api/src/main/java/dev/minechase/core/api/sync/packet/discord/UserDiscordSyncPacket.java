package dev.minechase.core.api.sync.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.sync.model.SyncInformation;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserSyncPacket implements Packet {

    private final SyncInformation information;

    @Override
    public void receive() {
        CoreAPI.getInstance().getSyncHandler().onUserSynced(this.information);
    }
}
