package dev.minechase.core.api.sync.packet.website;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.sync.model.SyncInformation;
import dev.minechase.core.api.sync.model.WebsiteSyncInformation;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WebsiteDiscordSyncPacket implements Packet {

    private final WebsiteSyncInformation information;

    @Override
    public void receive() {
        CoreAPI.getInstance().getWebsiteSyncHandler().onUserSynced(this.information);
    }
}
