package dev.minechase.core.api.grant.cache;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.mongodb.client.model.Filters;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.grant.model.Grant;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class GrantCacheLoader implements AsyncCacheLoader<UUID, List<Grant>> {

    @Override
    public CompletableFuture<? extends List<Grant>> asyncLoad(UUID owner, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Bson bson = Filters.eq("targetUUID", owner.toString());

                return CoreAPI.getInstance().getGrantHandler().getCollection().find(bson).map(Grant::new).into(new ArrayList<>());
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }, CoreAPI.POOL);
    }

}
