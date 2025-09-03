package dev.minechase.core.api.sync;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.sync.cache.DiscordSyncInformationCacheLoader;
import dev.minechase.core.api.sync.cache.PlayerSyncInformationCacheLoader;
import dev.minechase.core.api.sync.model.SyncCode;
import dev.minechase.core.api.sync.model.SyncInformation;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Getter
public class DiscordSyncHandler implements IModule {

    private MongoCollection<Document> discordCodeCollection, informationCollection;
    private List<SyncCode> syncCodes;
    private AsyncLoadingCache<UUID, SyncInformation> playerSyncInformation;
    private AsyncLoadingCache<String, SyncInformation> discordSyncInformation;

    @Override
    public void load() {
        this.syncCodes = new CopyOnWriteArrayList<>();
        this.playerSyncInformation = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .buildAsync(new PlayerSyncInformationCacheLoader());
        this.discordSyncInformation = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .buildAsync(new DiscordSyncInformationCacheLoader());

        this.discordCodeCollection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("DiscordSyncCodes");
        this.informationCollection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("SyncInformation");

        for (Document document : this.discordCodeCollection.find()) {
            this.syncCodes.add(new SyncCode(document));
        }
    }

    @Override
    public void unload() {

    }

    public CompletableFuture<SyncInformation> getSyncInformation(UUID playerUUID) {
        CompletableFuture<SyncInformation> presentInformation = this.playerSyncInformation.getIfPresent(playerUUID);

        if (presentInformation != null) {
            return presentInformation;
        }

        return this.playerSyncInformation.get(playerUUID);
    }

    public CompletableFuture<SyncInformation> getSyncInformation(String discordMemberId) {
        CompletableFuture<SyncInformation> presentInformation = this.discordSyncInformation.getIfPresent(discordMemberId);

        if (presentInformation != null) {
            return presentInformation;
        }

        return this.discordSyncInformation.get(discordMemberId);
    }

    public SyncCode getSyncCode(int code) {
        return this.syncCodes.stream().filter(syncCode -> syncCode.getCode() == code).findFirst().orElse(null);
    }

    public SyncCode getSyncCode(UUID playerUUID) {
        return this.syncCodes.stream().filter(syncCode -> syncCode.getPlayerUUID().equals(playerUUID)).findFirst().orElse(null);
    }

    public void updateCode(SyncCode code) {
        this.syncCodes.removeIf(other -> other.getCode() == code.getCode());
        this.syncCodes.add(code);
    }

    public void removeCode(SyncCode code) {
        this.syncCodes.removeIf(other -> other.getCode() == code.getCode());
    }

    public void updateInfo(SyncInformation syncInformation) {
        CompletableFuture<SyncInformation> infoIfPresent = this.playerSyncInformation.getIfPresent(syncInformation.getPlayerUUID());

        if (infoIfPresent != null) {
            infoIfPresent.whenCompleteAsync((info, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                }

                this.playerSyncInformation.put(syncInformation.getPlayerUUID(), CompletableFuture.completedFuture(syncInformation));
                this.discordSyncInformation.put(syncInformation.getDiscordMemberId(), CompletableFuture.completedFuture(syncInformation));
            });
        }
    }

    public void deleteCode(SyncCode code, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> deleteCode(code, false), CoreAPI.POOL);
            return;
        }

        this.discordCodeCollection.deleteOne(Filters.eq("code", code.getCode()));
    }

    public void saveCode(SyncCode code, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> saveCode(code, false), CoreAPI.POOL);
            return;
        }

        this.discordCodeCollection.replaceOne(Filters.eq("code", code.getCode()), code.toDocument(), new ReplaceOptions().upsert(true));
    }

    public void removeInfo(SyncInformation info) {
        this.discordSyncInformation.put(info.getDiscordMemberId(), CompletableFuture.completedFuture(null));
        this.playerSyncInformation.put(info.getPlayerUUID(), CompletableFuture.completedFuture(null));
    }

    public void deleteInfo(SyncInformation info, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> deleteInfo(info, false), CoreAPI.POOL);
            return;
        }

        this.informationCollection.deleteOne(Filters.eq("playerUUID", info.getPlayerUUID().toString()));
    }

    public void saveInfo(SyncInformation info, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> saveInfo(info, false), CoreAPI.POOL);
            return;
        }

        this.informationCollection.replaceOne(Filters.eq("playerUUID", info.getPlayerUUID().toString()), info.toDocument(), new ReplaceOptions().upsert(true));
    }

    public void onUserSynced(SyncInformation information) {

    }

}
