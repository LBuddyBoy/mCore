package dev.minechase.core.api.grant.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.grant.model.Grant;
import dev.minechase.core.api.packet.ServerResponsePacket;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GrantUpdatePacket extends ServerResponsePacket {

    private final Grant grant;
    private final String executeServer;

    public GrantUpdatePacket(Grant grant) {
        this.grant = grant;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getGrantHandler().updateGrant(this.grant);
        CoreAPI.getInstance().getGrantHandler().saveGrant(this.grant, true);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getGrantHandler().updateGrant(this.grant);
    }

}
