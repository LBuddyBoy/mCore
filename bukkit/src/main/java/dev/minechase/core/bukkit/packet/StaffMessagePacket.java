package dev.minechase.core.bukkit.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.bukkit.CoreConstants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StaffMessagePacket implements Packet {

    private final List<String> messages = new ArrayList<>();

    public StaffMessagePacket(String message) {
        this.messages.add(message);
    }

    public StaffMessagePacket(List<String> messages) {
        this.messages.addAll(messages);
    }

    @Override
    public void receive() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission(CoreConstants.STAFF_PERM)) continue;

            this.messages.forEach(player::sendMessage);
        }

        this.messages.forEach(s -> Bukkit.getConsoleSender().sendMessage(s));
    }

}
