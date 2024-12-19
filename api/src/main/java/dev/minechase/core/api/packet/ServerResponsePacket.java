package dev.minechase.core.api.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.minechase.core.api.CoreAPI;

public abstract class ServerResponsePacket implements Packet {

    public abstract String getExecuteServer();
    public abstract void onReceiveExecuteServer();
    public abstract void onReceiveOtherServer();

    @Override
    public void receive() {
        if (CoreAPI.getInstance().getServerName().equalsIgnoreCase(this.getExecuteServer())) {
            this.onReceiveExecuteServer();
            return;
        }

        this.onReceiveOtherServer();
    }

}
