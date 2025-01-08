package dev.minechase.core.velocity.packet;

import com.velocitypowered.api.proxy.Player;
import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.util.CC;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class PlayerKickPacket implements Packet {

    private final UUID playerUUID;
    private final String kickMessage;

    @Override
    public void receive() {
        CoreVelocity.getInstance().getProxy().getPlayer(this.playerUUID).ifPresentOrElse(player -> {
            player.disconnect(CC.translate(this.kickMessage));
        }, () -> CoreVelocity.getInstance().getLogger().warning("Tried to kick " + UUIDUtils.getName(playerUUID) + ", but they aren't online."));
    }

}
