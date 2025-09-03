package dev.minechase.core.api.sync.model;

import dev.minechase.core.api.api.Documented;
import lombok.Getter;
import org.bson.Document;

import java.util.UUID;

@Getter
public class WebsiteSyncInformation extends Documented {

    private final UUID playerUUID;
    private final String websiteUserId;
    private final long syncedAt;

    public WebsiteSyncInformation(UUID playerUUID, String websiteUserId) {
        this.playerUUID = playerUUID;
        this.websiteUserId = websiteUserId;
        this.syncedAt = System.currentTimeMillis();
    }

    public WebsiteSyncInformation(Document document) {
        this.playerUUID = this.deserializeUUID(document.getString("playerUUID"));
        this.websiteUserId = document.getString("websiteUserId");
        this.syncedAt = document.getLong("syncedAt");
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("playerUUID", this.playerUUID.toString())
                .append("websiteUserId", this.websiteUserId)
                .append("syncedAt", this.syncedAt)
                ;
    }
}
