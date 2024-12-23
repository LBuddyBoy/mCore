package dev.minechase.core.api.server.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.server.model.CoreServer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ServerUpdatePacket extends ServerResponsePacket {

    private final CoreServer server;

    @Override
    public String getExecuteServer() {
        return CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getServerHandler().updateServer(this.server);
        CoreAPI.getInstance().getServerHandler().saveServer(this.server);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getServerHandler().updateServer(this.server);
    }

}
