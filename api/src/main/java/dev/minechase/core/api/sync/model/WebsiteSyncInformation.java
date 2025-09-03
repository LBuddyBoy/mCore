package dev.minechase.core.api.sync.model;

import dev.minechase.core.api.api.Documented;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;

import java.util.UUID;

@Getter
public class SyncInformation extends Documented {

    private final UUID playerUUID;
    private final String discordMemberId;
    private final long syncedAt;

    public SyncInformation(UUID playerUUID, String discordMemberId) {
        this.playerUUID = playerUUID;
        this.discordMemberId = discordMemberId;
        this.syncedAt = System.currentTimeMillis();
    }

    public SyncInformation(Document document) {
        this.playerUUID = this.deserializeUUID(document.getString("playerUUID"));
        this.discordMemberId = document.getString("discordMemberId");
        this.syncedAt = document.getLong("syncedAt");
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("playerUUID", this.playerUUID.toString())
                .append("discordMemberId", this.discordMemberId)
                .append("syncedAt", this.syncedAt)
                ;
    }
}
