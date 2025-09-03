package dev.minechase.core.api.sync.packet.discord;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.sync.model.SyncCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DiscordSyncCodeUpdatePacket extends ServerResponsePacket {

    private final SyncCode code;
    private final String executeServer;

    public DiscordSyncCodeUpdatePacket(SyncCode code) {
        this.code = code;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getDiscordSyncHandler().updateCode(this.code);
        CoreAPI.getInstance().getDiscordSyncHandler().saveCode(this.code, true);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getDiscordSyncHandler().updateCode(this.code);
    }

}
