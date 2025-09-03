package dev.minechase.core.api.sync.packet.website;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.sync.model.SyncCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WebsiteSyncCodeDeletePacket extends ServerResponsePacket {

    private final SyncCode code;
    private final String executeServer;

    public WebsiteSyncCodeDeletePacket(SyncCode code) {
        this.code = code;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getWebsiteSyncHandler().removeCode(this.code);
        CoreAPI.getInstance().getWebsiteSyncHandler().deleteCode(this.code, true);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getWebsiteSyncHandler().removeCode(this.code);
    }

}
