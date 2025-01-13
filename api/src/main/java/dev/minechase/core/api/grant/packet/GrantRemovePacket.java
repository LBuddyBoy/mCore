package dev.minechase.core.api.grant.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.grant.model.Grant;
import dev.minechase.core.api.packet.ServerResponsePacket;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class GrantRemovePacket implements Packet {

    private final Grant grant;

    @Override
    public void receive() {
        CoreAPI.getInstance().getGrantHandler().onGrantRemoved(this.grant);
    }

}
