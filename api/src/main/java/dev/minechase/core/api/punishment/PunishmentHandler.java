package dev.minechase.core.api.punishment;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.IExpirable;
import dev.minechase.core.api.punishment.cache.PunishmentCacheLoader;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.model.PunishmentProof;
import dev.minechase.core.api.punishment.model.PunishmentType;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Getter
public class PunishmentHandler implements IModule {

    public static final TypeToken<List<PunishmentProof>> PROOF = new TypeToken<>() {};

    private MongoCollection<Document> collection;
    private AsyncLoadingCache<UUID, List<Punishment>> punishments;

    @Override
    public void load() {
        this.punishments = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .buildAsync(new PunishmentCacheLoader());

        this.collection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("Punishments");
    }

    @Override
    public void unload() {

    }

    public void updatePunishment(Punishment punishment) {
        CompletableFuture<List<Punishment>> punishmentsIfPresent = this.punishments.getIfPresent(punishment.getTargetUUID());

        if (punishmentsIfPresent != null) {
            punishmentsIfPresent.whenCompleteAsync((punishments, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                }

                punishments.removeIf(p -> p.getId().equals(punishment.getId()));
                punishments.add(punishment);
                this.punishments.put(punishment.getTargetUUID(), CompletableFuture.completedFuture(punishments));
            });
        }
    }

    public void savePunishment(Punishment punishment, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> savePunishment(punishment, false), CoreAPI.POOL);
            return;
        }

        this.collection.replaceOne(Filters.eq("id", punishment.getId().toString()), punishment.toDocument(), new ReplaceOptions().upsert(true));
    }

    public CompletableFuture<List<Punishment>> getPunishments(UUID targetUUID) {
        CompletableFuture<List<Punishment>> punishments = this.punishments.getIfPresent(targetUUID);

        if (punishments != null) return punishments;

        return this.punishments.get(targetUUID);
    }

    public CompletableFuture<List<Punishment>> getPunishmentsByType(UUID targetUUID, PunishmentType type) {
        return this.getPunishments(targetUUID).thenApplyAsync((punishments) -> punishments.stream().filter(punishment -> punishment.getType() == type).toList());
    }

    public CompletableFuture<List<Punishment>> getActivePunishments(UUID targetUUID) {
        return this.getPunishments(targetUUID).thenApplyAsync((punishments) -> punishments.stream().filter(IExpirable::isActive).toList());
    }

    public CompletableFuture<List<Punishment>> getActivePunishmentsByType(UUID targetUUID, PunishmentType type) {
        return this.getPunishments(targetUUID).thenApplyAsync((punishments) -> punishments.stream().filter(punishment -> punishment.isActive() && punishment.getType() == type).toList());
    }

}
