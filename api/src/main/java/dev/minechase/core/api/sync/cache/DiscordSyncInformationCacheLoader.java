package dev.minechase.core.api.sync.cache;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.mongodb.client.model.Filters;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.sync.model.SyncInformation;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DiscordSyncInformationCacheLoader implements AsyncCacheLoader<String, SyncInformation> {

    @Override
    public CompletableFuture<? extends SyncInformation> asyncLoad(String owner, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Bson bson = Filters.eq("discordMemberId", owner);
                Document document = CoreAPI.getInstance().getSyncHandler().getInformationCollection().find(bson).first();

                return document == null ? null : new SyncInformation(document);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }, CoreAPI.POOL);
    }

}
