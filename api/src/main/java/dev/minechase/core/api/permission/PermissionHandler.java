package dev.minechase.core.api.permission;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.ScopedPermission;
import dev.minechase.core.api.permission.cache.PermissionCacheLoader;
import dev.minechase.core.api.permission.packet.PermissionUpdatePacket;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.packet.PunishmentUpdatePacket;
import lombok.Getter;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Getter
public class PermissionHandler implements IModule {

    private MongoCollection<Document> collection;
    private AsyncLoadingCache<UUID, Map<String, ScopedPermission>> permissions;

    @Override
    public void load() {
        this.permissions = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .buildAsync(new PermissionCacheLoader());

        this.collection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("Permissions");
    }

    @Override
    public void unload() {

    }

    public void update(ScopedPermission permission) {
        CompletableFuture<Map<String, ScopedPermission>> permissionsIfPresent = this.permissions.getIfPresent(permission.getTargetUUID());

        if (permissionsIfPresent != null) {
            permissionsIfPresent.whenCompleteAsync((permissions, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                }

                permissions.put(permission.getPermissionNode(), permission);
                this.permissions.put(permission.getTargetUUID(), CompletableFuture.completedFuture(permissions));
            });
        }
    }

    public void save(ScopedPermission permission, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> save(permission, false), CoreAPI.POOL);
            return;
        }

        this.collection.replaceOne(Filters.eq("id", permission.getId()), permission.toDocument(), new ReplaceOptions().upsert(true));
    }

    public CompletableFuture<Map<String, ScopedPermission>> getPermissions(UUID playerUUID) {
        CompletableFuture<Map<String, ScopedPermission>> permissionsIfPresent = this.permissions.getIfPresent(playerUUID);

        if (permissionsIfPresent != null) {
            return permissionsIfPresent.thenApplyAsync(this::updateExpiry);
        }

        return this.permissions.get(playerUUID);
    }

    public Map<String, ScopedPermission> updateExpiry(Map<String, ScopedPermission> permissions) {
        for (ScopedPermission permission : permissions.values()) {
            if (permission.isRemoved()) continue;
            if (!permission.isExpired()) continue;

            permission.setRemovedAt(System.currentTimeMillis());
            permission.setRemovedBy(null);
            permission.setRemovedReason("Expired");

            new PermissionUpdatePacket(permission).send();
        }

        return permissions;
    }

    public void updatePermissions(UUID playerUUID) {
        CoreAPI.getInstance().getLogger().warning("Tried updating " + playerUUID + " permissions, but there's no API implementation.");
    }
    
}
