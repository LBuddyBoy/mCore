package dev.minechase.core.api.punishment.cache;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.mongodb.client.model.Filters;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.punishment.model.Punishment;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PunishmentCacheLoader implements AsyncCacheLoader<UUID, List<Punishment>> {

    @Override
    public CompletableFuture<? extends List<Punishment>> asyncLoad(UUID owner, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Bson bson = Filters.eq("targetUUID", owner.toString());

                return CoreAPI.getInstance().getPunishmentHandler().getCollection().find(bson).map(Punishment::new).into(new ArrayList<>());
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }, CoreAPI.POOL);
    }

}
