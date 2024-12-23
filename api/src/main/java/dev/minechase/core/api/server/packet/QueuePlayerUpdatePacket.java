package dev.minechase.core.api.server.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.server.model.QueuePlayer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class QueuePlayerUpdatePacket extends ServerResponsePacket {

    private final QueuePlayer player;

    @Override
    public String getExecuteServer() {
        return CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getServerHandler().updateQueuePlayer(this.player);
        CoreAPI.getInstance().getServerHandler().saveQueuePlayer(this.player);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getServerHandler().updateQueuePlayer(this.player);
    }

}
