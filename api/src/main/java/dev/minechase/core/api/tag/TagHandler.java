package dev.minechase.core.api.tag;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.data.impl.MongoDataStorage;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.IScoped;
import dev.minechase.core.api.tag.model.Tag;
import lombok.Getter;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Getter
public class TagHandler implements IModule {

    private final Map<UUID, Tag> tags;
    private MongoCollection<Document> collection;

    public TagHandler() {
        this.tags = new HashMap<>();
    }

    @Override
    public void load() {
        this.collection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("Tags");

        for (Document document : this.collection.find()) {
            Tag tag = new Tag(document);

            this.tags.put(tag.getId(), tag);
        }
    }

    @Override
    public void unload() {

    }

    public Tag getTag(String name) {
        return this.tags.values().stream().filter(tag -> tag.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Tag getLocalTag(String name) {
        return this.getLocalTags().values().stream().filter(tag -> tag.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Map<UUID, Tag> getLocalTags() {
        return this.tags.values().stream().filter(IScoped::isValidLocal).collect(Collectors.toMap(
                Tag::getId,
                tag -> tag
        ));
    }

    public void updateTag(Tag tag) {
        this.tags.put(tag.getId(), tag);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag.getId());
    }

    public void deleteTag(Tag tag) {
        this.collection.deleteOne(Filters.eq("id", tag.getId().toString()));
    }

    public void saveTag(Tag tag) {
        this.collection.replaceOne(Filters.eq("id", tag.getId().toString()), tag.toDocument(), new ReplaceOptions().upsert(true));
    }

}
