package dev.minechase.core.bukkit.model;

import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.util.ExceptedFuture;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.mod.ModModeHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class AsyncCorePlayer {

    private final String name;

    public AsyncCorePlayer(String name) {
        this.name = name;
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    public boolean isVanished() {
        return isOnline() && getPlayer().hasMetadata(ModModeHandler.VANISHED_METADATA);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.name);
    }

    public ExceptedFuture<UUID> getUUID() {
        return UUIDUtils.fetchUUID(this.name);
    }

    /**
     * Requests a user asynchronously to mitigate thread blocking when creating new users.
     *
     * @return Future User of a player by NAME this can be null and will produce an error if that player is not cached or doesn't have a Minecraft Account.
     */
    public ExceptedFuture<User> getUser() {
        return new ExceptedFuture<>(CorePlugin.getInstance().getUserHandler().getOrCreateAsync(this.name));
    }

}
