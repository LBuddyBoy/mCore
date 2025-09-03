package dev.minechase.core.api.sync.packet.discord;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.sync.model.SyncInformation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DiscordSyncInformationUpdatePacket extends ServerResponsePacket {

    private final SyncInformation information;
    private final String executeServer;

    public DiscordSyncInformationUpdatePacket(SyncInformation information) {
        this.information = information;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getDiscordSyncHandler().updateInfo(this.information);
        CoreAPI.getInstance().getDiscordSyncHandler().saveInfo(this.information, true);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getDiscordSyncHandler().updateInfo(this.information);
    }

}
