package dev.minechase.core.bukkit.model;

import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class CorePlayer {

    private final UUID uuid;
    private String name;

    public CorePlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public CompletableFuture<User> getUser() {
        return CorePlugin.getInstance().getUserHandler().getOrCreateAsync(this.uuid);
    }

}
