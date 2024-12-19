package dev.minechase.core.api.user;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.cache.UUIDCache;
import dev.lbuddyboy.commons.api.util.HTTPUtils;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.user.model.User;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Getter
public class UserHandler implements IModule {

    private static final Pattern UUID_PATTERN = Pattern.compile("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)");

    private Map<UUID, User> users;
    private MongoCollection<Document> collection;

    @Override
    public void load() {
        this.users = new HashMap<>();
        this.collection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("Users");
    }

    @Override
    public void unload() {

    }

    public User getUser(UUID playerUUID) {
        return this.users.getOrDefault(playerUUID, null);
    }

    public CompletableFuture<User> getOrCreateAsync(String name) {
        if (UUIDCache.getNamesToUuids().containsKey(name.toLowerCase())) {
            return this.getOrCreateAsync(UUIDCache.getNamesToUuids().get(name.toLowerCase()));
        }

        return HTTPUtils.requestAsync("https://api.mojang.com/users/profiles/minecraft/" + name).thenApplyAsync(
                (jsonResponse) -> {
                    JsonObject json = APIConstants.PARSER.parse(jsonResponse).getAsJsonObject();
                    String uuidString = UUID_PATTERN.matcher(json.get("id").getAsString()).replaceAll("$1-$2-$3-$4-$5");

                    return loadUser(UUID.fromString(uuidString), json.get("name").getAsString());
                }
        ).exceptionally(throwable -> {
            if (throwable != null) throwable.printStackTrace();

            return null;
        });
    }

    public CompletableFuture<User> getOrCreateAsync(UUID uuid) {
        if (this.users.containsKey(uuid)) return CompletableFuture.completedFuture(this.users.get(uuid));

        return HTTPUtils.requestAsync("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "") + "?unsigned=false").thenApplyAsync(
                (jsonResponse) -> {
                    JsonObject json = APIConstants.PARSER.parse(jsonResponse).getAsJsonObject();

                    return loadUser(uuid, json.get("name").getAsString());
                }
        ).exceptionally(throwable -> {
            if (throwable != null) throwable.printStackTrace();

            return null;
        });
    }

    public User loadUser(UUID uuid, String name) {
        User user = new User(uuid, name);
        Document document = this.collection.find(Filters.eq("uniqueId", uuid.toString())).first();

        if (document != null) {
            user.setFirstJoinAt(document.getLong("firstJoinedAt"));
        } else {
            this.saveUser(user);
            return user;
        }

        return user;
    }

    public void saveUser(User user) {
        Bson query = Filters.eq("uuid", user.getUniqueId().toString());
        Document document = this.collection.find(query).first();

        if (document == null) document = new Document();

        document.put("uniqueId", user.getUniqueId().toString());
        document.put("name", user.getName());
        document.put("firstJoinedAt", user.getFirstJoinAt());

        this.collection.replaceOne(query, document, new ReplaceOptions().upsert(true));
    }

}
