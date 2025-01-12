package dev.minechase.core.api.note.cache;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.mongodb.client.model.Filters;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.grant.model.Grant;
import dev.minechase.core.api.note.model.Note;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class NoteCacheLoader implements AsyncCacheLoader<UUID, List<Note>> {

    @Override
    public CompletableFuture<? extends List<Note>> asyncLoad(UUID owner, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Bson bson = Filters.eq("targetUUID", owner.toString());

                return CoreAPI.getInstance().getNoteHandler().getCollection().find(bson).map(Note::new).into(new ArrayList<>());
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }, CoreAPI.POOL);
    }

}
