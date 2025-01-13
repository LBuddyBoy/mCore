package dev.minechase.core.api.rank;

import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.ScopedPermission;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.rank.packet.RankUpdatePacket;
import lombok.Getter;
import org.bson.Document;

import java.util.*;

@Getter
public class RankHandler implements IModule {

    public static final TypeToken<List<ScopedPermission>> SCOPED_PERMISSIONS = new TypeToken<>() {};

    private final Map<UUID, Rank> ranks;
    private MongoCollection<Document> collection;

    public RankHandler() {
        this.ranks = new HashMap<>();
    }

    @Override
    public void load() {
        this.collection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("Ranks");

        for (Document document : this.collection.find()) {
            Rank rank = new Rank(document);

            this.ranks.put(rank.getId(), rank);
        }

        this.getDefaultRank();
    }

    @Override
    public void unload() {

    }

    public Rank getDefaultRank() {
        Rank defaultRank = this.getSortedRanks().stream().filter(Rank::isDefaultRank).findFirst().orElse(null);

        if (defaultRank == null) {
            defaultRank = new Rank("Default");
            defaultRank.setDefaultRank(true);

            new RankUpdatePacket(defaultRank).send();
            this.saveRank(defaultRank);
        }

        return defaultRank;
    }

    public Rank getRank(String name) {
        return this.ranks.values().stream().filter(rank -> rank.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Rank getRankById(UUID id) {
        return this.ranks.getOrDefault(id, null);
    }

    public void removeRank(Rank rank) {
        this.ranks.remove(rank.getId());
    }

    public void updateRank(Rank rank) {
        this.ranks.put(rank.getId(), rank);
    }

    public void deleteRank(Rank rank) {
        this.collection.deleteOne(Filters.eq("id", rank.getId().toString()));
    }

    public void saveRank(Rank rank) {
        this.collection.replaceOne(Filters.eq("id", rank.getId().toString()), rank.toDocument(), new ReplaceOptions().upsert(true));
    }

    public List<Rank> getSortedRanks() {
        return this.ranks.values().stream().sorted(Comparator.comparingInt(Rank::getWeight)).toList();
    }

}
