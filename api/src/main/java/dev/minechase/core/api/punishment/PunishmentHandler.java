package dev.minechase.core.api.punishment;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.IExpirable;
import dev.minechase.core.api.punishment.cache.PunishmentCacheLoader;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.model.PunishmentProof;
import dev.minechase.core.api.punishment.model.PunishmentSnapshot;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.api.punishment.packet.PunishmentUpdatePacket;
import lombok.Getter;
import org.bson.Document;

import java.util.ArrayList;
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
        this.collection.createIndex(new Document("targetUUID", 1));
        this.collection.createIndex(new Document("targetIp", 1));
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

    public List<Punishment> updatePunishmentExpiry(List<Punishment> punishments) {
        for (Punishment punishment : punishments) {
            if (punishment.isRemoved()) continue;
            if (!punishment.isExpired()) continue;

            punishment.remove(null, "Expired");
            new PunishmentUpdatePacket(punishment).send();
        }

        return punishments;
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

        if (punishments != null) {
            return punishments.thenApplyAsync(this::updatePunishmentExpiry);
        }

        return this.punishments.get(targetUUID).thenApplyAsync(this::updatePunishmentExpiry);
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

    public CompletableFuture<List<Punishment>> fetchAllPunishments() {
        return CompletableFuture.supplyAsync(() -> this.collection.find().map(Punishment::new).into(new ArrayList<>()), CoreAPI.POOL);
    }

    public CompletableFuture<List<Punishment>> fetchPunishmentsRelating(UUID targetUUID, String ipAddress) {
        return this.fetchAllPunishments().thenApplyAsync(punishments -> punishments.stream().filter(punishment -> (punishment.isIpRelated() && punishment.getTargetIp().equals(ipAddress)) || (punishment.getTargetUUID().equals(targetUUID))).toList());
    }

    /**
     *
     * Fetches all punishments of a player & their ip address, then maps them to a snapshot
     *
     * @param targetUUID targets unique id
     * @param ipAddress ip address of the target
     *
     * @return Completable future of punishment snapshots
     */

    public CompletableFuture<List<PunishmentSnapshot>> fetchSnapshotsRelating(UUID targetUUID, String ipAddress) {
        return this.fetchAllPunishments().thenApplyAsync(punishments -> punishments
                .stream()
                .filter(punishment -> (punishment.isIpRelated() && punishment.getTargetIp().equals(ipAddress)) || (punishment.getTargetUUID().equals(targetUUID)))
                .map(punishment -> new PunishmentSnapshot(punishment, (punishment.isIpRelated() && punishment.getTargetIp().equals(ipAddress)) && !punishment.getTargetUUID().equals(targetUUID)))
                .toList()
        );
    }

    public CompletableFuture<List<PunishmentSnapshot>> fetchSnapshotsRelating(String ipAddress) {
        return this.fetchAllPunishments().thenApplyAsync(punishments -> punishments
                .stream()
                .filter(punishment -> (punishment.isIpRelated() && punishment.getTargetIp().equals(ipAddress)))
                .map(punishment -> new PunishmentSnapshot(punishment, true))
                .toList()
        );
    }

}
