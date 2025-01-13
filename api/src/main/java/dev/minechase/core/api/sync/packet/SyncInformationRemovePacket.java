package dev.minechase.core.api.sync.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.sync.model.SyncInformation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SyncInformationRemovePacket extends ServerResponsePacket {

    private final SyncInformation information;
    private final String executeServer;

    public SyncInformationRemovePacket(SyncInformation information) {
        this.information = information;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getSyncHandler().removeInfo(this.information);
        CoreAPI.getInstance().getSyncHandler().deleteInfo(this.information, true);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getSyncHandler().removeInfo(this.information);
    }

}
