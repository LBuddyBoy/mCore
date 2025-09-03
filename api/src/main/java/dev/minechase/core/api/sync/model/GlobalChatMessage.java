package dev.minechase.core.api.sync.model;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class GlobalChatMessage {

    private final UUID playerUUID;
    private final String playerName, serverName, message;
    private final long sentAt;

    public JsonObject toJSON() {
        JsonObject object = new JsonObject();

        object.addProperty("playerUUID", this.playerUUID.toString());
        object.addProperty("playerName", this.playerName);
        object.addProperty("serverName", this.serverName);
        object.addProperty("message", this.message);
        object.addProperty("sentAt", this.sentAt);

        return object;
    }

}
