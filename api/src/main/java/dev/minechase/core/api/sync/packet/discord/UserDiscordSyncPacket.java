package dev.minechase.core.api.sync.packet.discord;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.sync.model.SyncInformation;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserDiscordSyncPacket implements Packet {

    private final SyncInformation information;

    @Override
    public void receive() {
        CoreAPI.getInstance().getDiscordSyncHandler().onUserSynced(this.information);
    }
}
