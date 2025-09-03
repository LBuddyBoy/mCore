package dev.minechase.core.bukkit.api;

import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.api.server.ServerHandler;
import dev.minechase.core.bukkit.CorePlugin;
import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
public class BukkitServerHandler extends ServerHandler {

    private boolean rebooted = false;

    @Override
    public void load() {
        super.load();

        if (getLocalServer() == null) return;

        Tasks.run(() -> {
            getLocalServer().setStartedAt(System.currentTimeMillis());
            CorePlugin.getInstance().updateLocalServer();
        });
    }

    @Override
    public void reboot() {
        super.reboot();

        this.rebooted = true;

        Bukkit.shutdown();
    }
}
