package dev.minechase.core.api.iphistory.cache;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.mongodb.client.model.Filters;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.iphistory.model.HistoricalIP;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class IPHistoryCacheLoader implements AsyncCacheLoader<UUID, Map<String, HistoricalIP>> {

    @Override
    public CompletableFuture<? extends Map<String, HistoricalIP>> asyncLoad(UUID owner, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Bson bson = Filters.eq("playerUUID", owner.toString());
                Map<String, HistoricalIP> ips = new ConcurrentHashMap<>();

                for (Document document : CoreAPI.getInstance().getIpHistoryHandler().getCollection().find(bson)) {
                    HistoricalIP historicalIP = new HistoricalIP(document);

                    ips.put(historicalIP.getIpAddress(), historicalIP);
                }

                return ips;
            } catch (Exception e) {
                e.printStackTrace();
                return new ConcurrentHashMap<>();
            }
        }, CoreAPI.POOL);
    }

}
