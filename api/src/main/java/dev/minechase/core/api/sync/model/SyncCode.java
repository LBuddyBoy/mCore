package dev.minechase.core.api.sync.model;

import dev.minechase.core.api.api.Documented;
import lombok.Getter;
import org.bson.Document;

import java.util.UUID;

@Getter
public class SyncCode extends Documented {

    private final UUID playerUUID;
    private final int code;
    private final long createdAt;

    public SyncCode(UUID playerUUID, int code) {
        this.playerUUID = playerUUID;
        this.code = code;
        this.createdAt = System.currentTimeMillis();
    }

    public SyncCode(Document document) {
        this.playerUUID = this.deserializeUUID(document.getString("playerUUID"));
        this.code = document.getInteger("code");
        this.createdAt = document.getLong("createdAt");
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("playerUUID", this.playerUUID.toString())
                .append("code", this.code)
                .append("createdAt", this.createdAt)
                ;
    }
}
