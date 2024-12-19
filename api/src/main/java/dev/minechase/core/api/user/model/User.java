package dev.minechase.core.api.user.model;

import dev.minechase.core.api.CoreAPI;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
public class User {

    private final UUID uniqueId;
    private String name;
    private long firstJoinAt;

    public User(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public User(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    public boolean hasPlayedBefore() {
        return this.firstJoinAt > 0;
    }

    public void save(boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> save(true), CoreAPI.POOL);
            return;
        }

        CoreAPI.getInstance().getUserHandler().saveUser(this);
    }

}
