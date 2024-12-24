package dev.minechase.core.api.server.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.server.model.QueuePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueuePlayerRemovePacket extends ServerResponsePacket {

    private final QueuePlayer player;
    private final String executeServer;

    public QueuePlayerRemovePacket(QueuePlayer player) {
        this.player = player;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getServerHandler().removeQueuePlayer(this.player);
        CoreAPI.getInstance().getServerHandler().deleteQueuePlayer(this.player);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getServerHandler().removeQueuePlayer(this.player);
    }

}
