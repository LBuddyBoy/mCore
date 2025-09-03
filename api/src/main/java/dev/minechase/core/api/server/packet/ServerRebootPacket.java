package dev.minechase.core.api.server.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.minechase.core.api.CoreAPI;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/23/2025
 */
public class ServerRebootPacket implements Packet {

    private final String serverName;

    public ServerRebootPacket(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public void receive() {
        if (!CoreAPI.getInstance().getServerName().equals(this.serverName)) return;

        CoreAPI.getInstance().getServerHandler().reboot();
    }

}
