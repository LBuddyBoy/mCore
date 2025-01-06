package dev.minechase.core.api.server.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.server.model.CoreServer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ServerUpdatePacket extends ServerResponsePacket {

    private final CoreServer server;
    private final String executeServer;

    public ServerUpdatePacket(CoreServer server) {
        this.server = server;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getServerHandler().updateServer(this.server);
        CoreAPI.getInstance().getServerHandler().saveServer(this.server, true);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getServerHandler().updateServer(this.server);
    }

}
