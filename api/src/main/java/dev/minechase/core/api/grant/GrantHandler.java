package dev.minechase.core.api.grant;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.grant.cache.GrantCacheLoader;
import dev.minechase.core.api.grant.comparator.GrantComparator;
import dev.minechase.core.api.grant.grant.Grant;
import dev.minechase.core.api.grant.packet.GrantUpdatePacket;
import dev.minechase.core.api.punishment.cache.PunishmentCacheLoader;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.model.PunishmentProof;
import dev.minechase.core.api.punishment.packet.PunishmentUpdatePacket;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.user.model.User;
import lombok.Getter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Getter
public class GrantHandler implements IModule {

    private MongoCollection<Document> collection;
    private AsyncLoadingCache<UUID, List<Grant>> grants;

    @Override
    public void load() {
        this.grants = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .buildAsync(new GrantCacheLoader());

        this.collection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("Grants");
    }

    @Override
    public void unload() {

    }

    public CompletableFuture<List<Grant>> fetchAllGrants() {
        return CompletableFuture.supplyAsync(() -> this.collection.find().map(Grant::new).into(new ArrayList<>()), CoreAPI.POOL);
    }

    public void updateGrant(Grant grant) {
        CompletableFuture<List<Grant>> grantsIfPresent = this.grants.getIfPresent(grant.getTargetUUID());

        if (grantsIfPresent != null) {
            grantsIfPresent.whenCompleteAsync((grants, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                }

                grants.removeIf(p -> p.getId().equals(grant.getId()));
                grants.add(grant);

                this.grants.put(grant.getTargetUUID(), CompletableFuture.completedFuture(grants));
            });
        }
    }

    public List<Grant> updateGrantExpiry(List<Grant> grants) {
        for (Grant grant : grants) {
            if (grant.isRemoved()) continue;
            if (!grant.isExpired()) continue;

            grant.remove(null, "Expired");
            new GrantUpdatePacket(grant).send();
        }

        return grants;
    }

    public void saveGrant(Grant grant, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> saveGrant(grant, false), CoreAPI.POOL);
            return;
        }

        this.collection.replaceOne(Filters.eq("id", grant.getId().toString()), grant.toDocument(), new ReplaceOptions().upsert(true));

        /**
         * Update user's grant everytime their grants are saved.
         */

        CoreAPI.getInstance().getUserHandler().getOrCreateAsync(grant.getTargetUUID()).whenCompleteAsync(((user, t) -> {
            if (t != null) {
                t.printStackTrace();
                return;
            }

            user.updateActiveGrant();
        }));
    }

    public CompletableFuture<List<Grant>> getGrants(UUID targetUUID) {
        CompletableFuture<List<Grant>> grantsIfPresent = this.grants.getIfPresent(targetUUID);

        if (grantsIfPresent != null) return grantsIfPresent.thenApplyAsync(this::updateGrantExpiry);

        return this.grants.get(targetUUID).thenApplyAsync(this::updateGrantExpiry);
    }

    public CompletableFuture<List<Grant>> getSortedGrants(UUID targetUUID) {
        return this.getGrants(targetUUID).thenApplyAsync(grants -> grants.stream().sorted(new GrantComparator()).toList());
    }

    public CompletableFuture<List<Grant>> getValidGrants(UUID targetUUID) {
        return this.getGrants(targetUUID).thenApplyAsync(grants -> grants.stream().filter(
                grant -> grant.isValidLocal() && !grant.isRemoved() && !grant.isExpired()
        ).sorted(Comparator.comparingInt(Grant::getWeight)).toList());
    }

}
