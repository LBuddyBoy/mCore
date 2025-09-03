package dev.minechase.core.api.sync.packet.discord;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.sync.model.SyncInformation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DiscordSyncInformationRemovePacket extends ServerResponsePacket {

    private final SyncInformation information;
    private final String executeServer;

    public DiscordSyncInformationRemovePacket(SyncInformation information) {
        this.information = information;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getDiscordSyncHandler().removeInfo(this.information);
        CoreAPI.getInstance().getDiscordSyncHandler().deleteInfo(this.information, true);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getDiscordSyncHandler().removeInfo(this.information);
    }

}
