package dev.minechase.core.api.sync.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.sync.model.SyncCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SyncCodeUpdatePacket extends ServerResponsePacket {

    private final SyncCode code;
    private final String executeServer;

    public SyncCodeUpdatePacket(SyncCode code) {
        this.code = code;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getSyncHandler().updateCode(this.code);
        CoreAPI.getInstance().getSyncHandler().saveCode(this.code, true);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getSyncHandler().updateCode(this.code);
    }

}
