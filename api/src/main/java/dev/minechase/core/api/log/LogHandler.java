package dev.minechase.core.api.log;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.log.model.CoreLog;
import dev.minechase.core.api.log.model.CoreLogType;
import dev.minechase.core.api.punishment.model.Punishment;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LogHandler implements IModule {

    private MongoCollection<Document> collection;

    @Override
    public void load() {
        this.collection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("Logs");
    }

    @Override
    public void unload() {

    }

    public void saveLog(CoreLog log) {
        CompletableFuture.runAsync(() -> this.collection.replaceOne(Filters.eq("id", log.getId().toString()), log.toDocument(), new ReplaceOptions().upsert(true)), CoreAPI.POOL);
    }

    public CompletableFuture<List<CoreLog>> getAllLogs() {
        return CompletableFuture.supplyAsync(() -> this.collection.find().map(document -> {
            CoreLogType type = CoreLogType.valueOf(document.getString("type"));

            return type.getCreationConsumer().apply(document);
        }).into(new ArrayList<>()), CoreAPI.POOL);
    }

}
