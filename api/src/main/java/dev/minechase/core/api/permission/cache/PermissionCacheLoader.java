package dev.minechase.core.api.permission.cache;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.mongodb.client.model.Filters;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.ScopedPermission;
import dev.minechase.core.api.iphistory.model.HistoricalIP;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class PermissionCacheLoader implements AsyncCacheLoader<UUID, Map<String, ScopedPermission>> {

    @Override
    public CompletableFuture<? extends Map<String, ScopedPermission>> asyncLoad(UUID owner, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Bson bson = Filters.eq("targetUUID", owner.toString());
                Map<String, ScopedPermission> permissions = new ConcurrentHashMap<>();

                for (Document document : CoreAPI.getInstance().getPermissionHandler().getCollection().find(bson)) {
                    ScopedPermission permission = new ScopedPermission(document);

                    permissions.put(permission.getPermissionNode(), permission);
                }

                return permissions;
            } catch (Exception e) {
                e.printStackTrace();
                return new ConcurrentHashMap<>();
            }
        }, CoreAPI.POOL);
    }

}
