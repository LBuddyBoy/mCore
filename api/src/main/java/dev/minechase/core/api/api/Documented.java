package dev.minechase.core.api.api;

import org.bson.Document;

import java.util.UUID;

public abstract class Documented {

    protected Documented() {
    }

    public Documented(Document document) {

    }

    public abstract Document toDocument();

    public UUID deserializeUUID(String uuidString) {
        return uuidString == null ? null : UUID.fromString(uuidString);
    }

    public String serializeUUID(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }

}
