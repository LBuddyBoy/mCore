package dev.minechase.core.api.prefix;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.IScoped;
import dev.minechase.core.api.prefix.model.Prefix;
import dev.minechase.core.api.prefix.model.Prefix;
import lombok.Getter;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class PrefixHandler implements IModule {

    private final Map<UUID, Prefix> prefixes;
    private MongoCollection<Document> collection;

    public PrefixHandler() {
        this.prefixes = new HashMap<>();
    }

    @Override
    public void load() {
        this.collection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("Prefixes");

        for (Document document : this.collection.find()) {
            Prefix prefix = new Prefix(document);

            this.prefixes.put(prefix.getId(), prefix);
        }
    }

    @Override
    public void unload() {

    }

    public Prefix getPrefix(String name) {
        return this.prefixes.values().stream().filter(prefix -> prefix.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Prefix getLocalPrefix(String name) {
        return this.getLocalPrefixes().values().stream().filter(prefix -> prefix.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Map<UUID, Prefix> getLocalPrefixes() {
        return this.prefixes.values().stream().filter(IScoped::isValidLocal).collect(Collectors.toMap(
                Prefix::getId,
                prefix -> prefix
        ));
    }

    public void updatePrefix(Prefix prefix) {
        this.prefixes.put(prefix.getId(), prefix);
    }

    public void removePrefix(Prefix prefix) {
        this.prefixes.remove(prefix.getId());
    }

    public void deletePrefix(Prefix prefix) {
        this.collection.deleteOne(Filters.eq("id", prefix.getId().toString()));
    }

    public void savePrefix(Prefix prefix) {
        this.collection.replaceOne(Filters.eq("id", prefix.getId().toString()), prefix.toDocument(), new ReplaceOptions().upsert(true));
    }

}
