package dev.minechase.core.bukkit.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.minechase.core.bukkit.CoreConstants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GlobalStaffMessagePacket implements Packet {

    private final List<String> messages = new ArrayList<>();

    public GlobalStaffMessagePacket(String message) {
        this.messages.add(message);
    }

    public GlobalStaffMessagePacket(List<String> messages) {
        this.messages.addAll(messages);
    }

    @Override
    public void receive() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission(CoreConstants.STAFF_PERM)) continue;

            this.messages.forEach(player::sendMessage);
        }
    }

}
