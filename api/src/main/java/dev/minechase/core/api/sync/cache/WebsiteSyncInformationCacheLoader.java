package dev.minechase.core.api.sync.cache;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.mongodb.client.model.Filters;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.sync.model.SyncInformation;
import dev.minechase.core.api.sync.model.WebsiteSyncInformation;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class WebsiteSyncInformationCacheLoader implements AsyncCacheLoader<String, WebsiteSyncInformation> {

    @Override
    public CompletableFuture<? extends WebsiteSyncInformation> asyncLoad(String owner, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Bson bson = Filters.eq("websiteMemberId", owner);
                Document document = CoreAPI.getInstance().getWebsiteSyncHandler().getInformationCollection().find(bson).first();

                return document == null ? null : new WebsiteSyncInformation(document);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }, CoreAPI.POOL);
    }

}
