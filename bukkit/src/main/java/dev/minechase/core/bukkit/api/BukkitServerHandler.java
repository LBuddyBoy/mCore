package dev.minechase.core.bukkit.api;

import dev.minechase.core.api.server.ServerHandler;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.server.model.ServerStatus;
import dev.minechase.core.api.server.packet.ServerUpdatePacket;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitServerHandler extends ServerHandler<Player> {

    @Override
    public void load() {
        super.load();

        if (getLocalServer() == null) return;

        getLocalServer().setStartedAt(System.currentTimeMillis());
        CorePlugin.getInstance().updateLocalServer();
    }
}
