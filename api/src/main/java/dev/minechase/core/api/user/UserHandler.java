package dev.minechase.core.api.user;

import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.cache.UUIDCache;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.grant.grant.Grant;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.user.model.UserMetadata;
import dev.minechase.core.api.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Getter
public class UserHandler implements IModule {

    private static final Pattern UUID_PATTERN = Pattern.compile("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)");
    private static final TypeToken<UserMetadata> METADATA = new TypeToken<>() {};

    private Map<UUID, User> users;
    private MongoCollection<Document> collection;

    @Override
    public void load() {
        this.users = new HashMap<>();
        this.collection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("Users");
    }

    @Override
    public void unload() {
        this.users.values().forEach(user -> user.save(false));
    }

    /**
     * Fetches a user based on the cache (This will not query any database)
     *
     * @param playerUUID players uuid to fetch
     * @return a user based on the cache
     */

    public User getUser(UUID playerUUID) {
        return this.users.getOrDefault(playerUUID, null);
    }

    public CompletableFuture<User> getOrCreateAsync(String name) {
        if (UUIDCache.getNamesToUuids().containsKey(name.toLowerCase())) {
            return this.getOrCreateAsync(UUIDCache.getNamesToUuids().get(name.toLowerCase()));
        }

        return UUIDUtils.fetchUUID(name).thenApplyAsync(uuid -> loadUser(uuid, name));
    }

    public CompletableFuture<User> getOrCreateAsync(UUID uuid) {
        if (this.users.containsKey(uuid)) return CompletableFuture.completedFuture(this.users.get(uuid));

        return UUIDUtils.fetchName(uuid).thenApplyAsync(name -> loadUser(uuid, name));
    }

    public CompletableFuture<List<User>> fetchUsersAsync() {
        return CompletableFuture.supplyAsync(() -> UUIDCache.getUuidToNames().entrySet().stream().map(entry -> loadUser(entry.getKey(), entry.getValue())).toList());
    }

    public User loadUser(UUID uuid, String name) {
        User user = new User(uuid, name);
        Document document = this.collection.find(Filters.eq("uniqueId", uuid.toString())).first();

        if (document != null) {
            user.setFirstJoinAt(document.getLong("firstJoinedAt"));
            user.setActiveGrant(new Grant(Document.parse(document.getString("activeGrant"))));
            user.setCurrentIpAddress(document.getString("currentIpAddress"));
            user.getIpHistory().addAll(document.getList("ipHistory", String.class, new ArrayList<>()));

            if (document.containsKey("persistentMetadata")) user.setPersistentMetadata(APIConstants.GSON.fromJson(document.getString("persistentMetadata"), METADATA.getType()));

        } else {
            user.setActiveGrant(Grant.DEFAULT_GRANT(uuid));
            this.saveUser(user);
            return user;
        }

        user.updateActiveGrant();

        return user;
    }

    public void saveUser(User user) {
        Bson query = Filters.eq("uniqueId", user.getUniqueId().toString());
        Document document = this.collection.find(query).first();

        if (document == null) document = new Document();

        document.put("uniqueId", user.getUniqueId().toString());
        document.put("name", user.getName());
        document.put("currentIpAddress", user.getCurrentIpAddress());
        document.put("ipHistory", user.getIpHistory());
        document.put("firstJoinedAt", user.getFirstJoinAt());
        document.put("activeGrant", user.getActiveGrant().toDocument().toJson());
        document.put("persistentMetadata", APIConstants.GSON.toJson(user.getPersistentMetadata(), METADATA.getType()));

        this.collection.replaceOne(query, document, new ReplaceOptions().upsert(true));
    }

}
