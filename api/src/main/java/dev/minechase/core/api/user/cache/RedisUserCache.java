package dev.minechase.core.api.user.cache;

import dev.lbuddyboy.commons.api.data.impl.RedisDataStorage;
import dev.minechase.core.api.user.model.User;
import org.bson.Document;

import java.util.UUID;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/23/2025
 */
public class RedisUserCache extends RedisDataStorage<UUID, String> {

    public RedisUserCache() {
        super("GlobalUserCache", UUID.class, String.class, true);
    }

    public User loadUser(UUID playerUUID) {
        String value = this.load(playerUUID);

        if (value == null) return null;

        Document document = Document.parse(value);
        if (document == null) return null;

        return new User(document);
    }

}
