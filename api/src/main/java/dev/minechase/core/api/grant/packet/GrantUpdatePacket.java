package dev.minechase.core.api.grant.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.grant.grant.Grant;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.punishment.model.Punishment;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GrantUpdatePacket extends ServerResponsePacket {

    private final Grant grant;

    @Override
    public String getExecuteServer() {
        return CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getGrantHandler().updateGrant(this.grant);
        CoreAPI.getInstance().getGrantHandler().saveGrant(this.grant);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getGrantHandler().updateGrant(this.grant);
    }

}
