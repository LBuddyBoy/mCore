package dev.minechase.core.api.sync.cache;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.mongodb.client.model.Filters;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.iphistory.model.HistoricalIP;
import dev.minechase.core.api.sync.model.SyncInformation;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class PlayerSyncInformationCacheLoader implements AsyncCacheLoader<UUID, SyncInformation> {

    @Override
    public CompletableFuture<? extends SyncInformation> asyncLoad(UUID owner, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Bson bson = Filters.eq("playerUUID", owner.toString());
                Document document = CoreAPI.getInstance().getSyncHandler().getCodeCollection().find(bson).first();

                return document == null ? null : new SyncInformation(document);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }, CoreAPI.POOL);
    }

}
