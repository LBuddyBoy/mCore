package dev.minechase.core.api.sync.packet.website;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.sync.model.SyncInformation;
import dev.minechase.core.api.sync.model.WebsiteSyncInformation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WebsiteSyncInformationRemovePacket extends ServerResponsePacket {

    private final WebsiteSyncInformation information;
    private final String executeServer;

    public WebsiteSyncInformationRemovePacket(WebsiteSyncInformation information) {
        this.information = information;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getWebsiteSyncHandler().removeInfo(this.information);
        CoreAPI.getInstance().getWebsiteSyncHandler().deleteInfo(this.information, true);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getWebsiteSyncHandler().removeInfo(this.information);
    }

}
