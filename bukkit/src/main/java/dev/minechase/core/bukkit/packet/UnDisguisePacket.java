package dev.minechase.core.bukkit.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.util.CommandUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class UnDisguisePacket extends ServerResponsePacket {

    private final UUID senderUUID, targetUUID;
    private final String executeServer;

    public UnDisguisePacket(UUID senderUUID, UUID targetUUID) {
        this.senderUUID = senderUUID;
        this.targetUUID = targetUUID;
        this.executeServer = CorePlugin.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        Player player = Bukkit.getPlayer(this.targetUUID);

        new PlayerMessagePacket(Arrays.asList(
                "&4" + UUIDUtils.getName(this.senderUUID) + "&c deactivated your disguise."
        ), this.targetUUID).send();

        if (player != null) {
            CorePlugin.getInstance().getUserHandler().undisguise(player);
            return;
        }

        CorePlugin.getInstance().getUserHandler().getOrCreateAsync(this.targetUUID).whenCompleteAsync(((user, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            user.undisguise();
            user.save(true);
        }));
    }

    @Override
    public void onReceiveOtherServer() {
        Player player = Bukkit.getPlayer(this.targetUUID);

        if (player != null) {
            CorePlugin.getInstance().getUserHandler().undisguise(player);
            return;
        }
    }
}
