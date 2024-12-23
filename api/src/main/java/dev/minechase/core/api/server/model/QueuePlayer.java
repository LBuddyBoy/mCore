package dev.minechase.core.api.server.model;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.Documented;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class QueuePlayer extends Documented {

    private final UUID playerUUID;
    private final String playerName;
    private String queueName;
    private int priority;
    private long offlineAt;

    @Setter private transient int position;

    public QueuePlayer(Document document) {
        this.playerUUID = this.deserializeUUID(document.getString("playerUUID"));
        this.playerName = document.getString("playerName");
        this.queueName = document.getString("queueName");
        this.priority = document.getInteger("priority");
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("playerUUID", this.serializeUUID(this.playerUUID))
                .append("playerName", this.playerName)
                .append("queueName", this.queueName)
                .append("priority", this.priority);
    }

    public boolean isOnline() {
        return this.offlineAt <= 0;
    }

    public CoreServer getServer() {
        return CoreAPI.getInstance().getServerHandler().getServer(this.queueName);
    }

}
