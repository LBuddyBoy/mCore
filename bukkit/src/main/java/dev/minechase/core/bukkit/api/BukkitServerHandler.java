package dev.minechase.core.bukkit.api;

import dev.minechase.core.api.server.ServerHandler;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.server.model.ServerStatus;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitServerHandler extends ServerHandler<Player> {

    @Override
    public boolean isJoinable(Player player, CoreServer server) {
        boolean bypass = player.hasPermission("core.queue.bypass");
        ServerStatus status = server.getStatus();

        if (status == ServerStatus.OFFLINE) return false;

        return status == ServerStatus.ONLINE || bypass;
    }

}
