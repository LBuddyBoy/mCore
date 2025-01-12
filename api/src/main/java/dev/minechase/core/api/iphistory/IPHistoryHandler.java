package dev.minechase.core.api.iphistory;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.iphistory.model.HistoricalIP;
import dev.minechase.core.api.iphistory.cache.IPHistoryCacheLoader;
import dev.minechase.core.api.iphistory.packet.HistoricalIPUpdatePacket;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Getter
public class IPHistoryHandler implements IModule {

    private MongoCollection<Document> collection;
    private AsyncLoadingCache<UUID, Map<String, HistoricalIP>> historicalIps;

    @Override
    public void load() {
        this.historicalIps = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .buildAsync(new IPHistoryCacheLoader());

        this.collection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("IPHistory");
    }

    @Override
    public void unload() {

    }

    public void update(HistoricalIP historicalIP) {
        CompletableFuture<Map<String, HistoricalIP>> ipsIfPresent = this.historicalIps.getIfPresent(historicalIP.getPlayerUUID());

        if (ipsIfPresent != null) {
            ipsIfPresent.whenCompleteAsync((ips, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                }

                ips.put(historicalIP.getIpAddress(), historicalIP);
                this.historicalIps.put(historicalIP.getPlayerUUID(), CompletableFuture.completedFuture(ips));
            });
        }
    }

    public void save(HistoricalIP historicalIP, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> save(historicalIP, false), CoreAPI.POOL);
            return;
        }

        this.collection.replaceOne(Filters.eq("id", historicalIP.getId().toString()), historicalIP.toDocument(), new ReplaceOptions().upsert(true));
    }

    public CompletableFuture<Map<String, HistoricalIP>> getHistoricalIps(UUID playerUUID) {
        CompletableFuture<Map<String, HistoricalIP>> ips = this.historicalIps.getIfPresent(playerUUID);

        if (ips != null) {
            return ips;
        }

        return this.historicalIps.get(playerUUID);
    }

    public void applyChange(UUID playerUUID, String ipAddress) {
        CoreAPI.getInstance().getIpHistoryHandler().getHistoricalIP(playerUUID, ipAddress, new HistoricalIP(playerUUID, ipAddress)).whenCompleteAsync((ip, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            ip.getLogins().add(System.currentTimeMillis());
            ip.setLastChangedAt(System.currentTimeMillis());

            new HistoricalIPUpdatePacket(ip).send();
        });
    }

    public void applyLogin(UUID playerUUID, String ipAddress) {
        CoreAPI.getInstance().getIpHistoryHandler().getHistoricalIP(playerUUID, ipAddress, new HistoricalIP(playerUUID, ipAddress)).whenCompleteAsync((ip, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            ip.getLogins().add(System.currentTimeMillis());

            new HistoricalIPUpdatePacket(ip).send();
        });
    }

    public CompletableFuture<HistoricalIP> getHistoricalIP(UUID playerUUID, String ipAddress) {
        return this.getHistoricalIps(playerUUID).thenApplyAsync(ips -> ips.get(ipAddress));
    }

    public CompletableFuture<HistoricalIP> getHistoricalIP(UUID playerUUID, String ipAddress, HistoricalIP def) {
        return this.getHistoricalIps(playerUUID).thenApplyAsync(ips -> ips.getOrDefault(ipAddress, def));
    }

}
