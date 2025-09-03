package dev.minechase.core.api.sync.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.sync.model.SyncCode;
import dev.minechase.core.api.sync.model.SyncInformation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SyncInformationUpdatePacket extends ServerResponsePacket {

    private final SyncInformation information;
    private final String executeServer;

    public SyncInformationUpdatePacket(SyncInformation information) {
        this.information = information;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getSyncHandler().updateInfo(this.information);
        CoreAPI.getInstance().getSyncHandler().saveInfo(this.information, true);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getSyncHandler().updateInfo(this.information);
    }

}
