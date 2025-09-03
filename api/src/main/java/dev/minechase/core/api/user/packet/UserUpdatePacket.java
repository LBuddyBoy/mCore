package dev.minechase.core.api.user.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.user.model.User;
import lombok.Getter;
import org.bson.Document;

import java.util.UUID;

@Getter
public class UserUpdatePacket implements Packet {

    private final UUID playerUUID;
    private final String json;
    private final String executeServer;

    public UserUpdatePacket(User user) {
        this.playerUUID = user.getUniqueId();
        this.json = user.toDocument().toJson();
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void receive() {
        if (!CoreAPI.getInstance().isProxy()) {
            if (!CoreAPI.getInstance().getServerName().equals(this.executeServer)) return;

            CoreAPI.getInstance().getUserHandler().getCache().save(this.playerUUID, this.json);
            return;
        }

        Document document = Document.parse(this.json);

        if (document == null) {
            return;
        }

        CoreAPI.getInstance().getUserHandler().getUsers().put(this.playerUUID, new User(document));
    }

}
