package dev.minechase.core.velocity.packet;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.util.CC;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;

import java.util.UUID;

@AllArgsConstructor
public class PlayerSendToServerPacket implements Packet {

    private final UUID playerUUID;
    private final String serverName;

    @Override
    public void receive() {
        CoreVelocity.getInstance().getProxy().getPlayer(this.playerUUID).ifPresentOrElse(player -> {
            CoreVelocity.getInstance().getProxy().getServer(this.serverName).ifPresentOrElse(server -> {
                player.createConnectionRequest(server).connect().whenCompleteAsync(((result, throwable) -> {
                    if (result.isSuccessful()) return;

                    if (throwable != null) {
                        throwable.printStackTrace();
                    }

                    player.sendMessage(CC.translate("&cYou were kicked from " + this.serverName + ": " + CC.translate(result.getReasonComponent().orElse(Component.text("None")))));

                }));
            }, () -> CoreVelocity.getInstance().getLogger().warning("Tried sending " + UUIDUtils.getName(this.playerUUID) + ", but the server is not registered."));
        }, () -> CoreVelocity.getInstance().getLogger().warning("Tried sending " + UUIDUtils.getName(this.playerUUID) + ", but they were not connected to the proxy."));
    }

}
