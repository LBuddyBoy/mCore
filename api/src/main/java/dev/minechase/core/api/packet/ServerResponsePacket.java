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
            CoreAPI.getInstance().getLogger().info("Received Execute Server: " + this.getClass().getName());
            return;
        }

        this.onReceiveOtherServer();
        CoreAPI.getInstance().getLogger().info("Received Other Server: " + this.getClass().getName());
    }

}
