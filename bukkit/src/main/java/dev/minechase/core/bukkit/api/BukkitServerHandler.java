package dev.minechase.core.bukkit.api;

import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.api.server.ServerHandler;
import dev.minechase.core.bukkit.CorePlugin;

public class BukkitServerHandler extends ServerHandler {

    @Override
    public void load() {
        super.load();

        if (getLocalServer() == null) return;

        Tasks.run(() -> {
            getLocalServer().setStartedAt(System.currentTimeMillis());
            CorePlugin.getInstance().updateLocalServer();
        });
    }
}
