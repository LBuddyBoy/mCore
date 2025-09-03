package dev.minechase.core.api.server.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.server.model.CoreServer;
import lombok.Getter;

@Getter
public class ServerDeletePacket extends ServerResponsePacket {

    private final CoreServer server;
    private final String executeServer;

    public ServerDeletePacket(CoreServer server) {
        this.server = server;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        if (CoreAPI.getInstance().getServerHandler() == null) return;

        CoreAPI.getInstance().getServerHandler().unregisterServer(this.server);
        CoreAPI.getInstance().getServerHandler().deleteServer(this.server);
    }

    @Override
    public void onReceiveOtherServer() {
        if (CoreAPI.getInstance().getServerHandler() == null) return;

        CoreAPI.getInstance().getServerHandler().unregisterServer(this.server);
    }

}
