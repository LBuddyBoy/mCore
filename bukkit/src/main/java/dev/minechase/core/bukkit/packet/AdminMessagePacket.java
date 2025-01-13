package dev.minechase.core.bukkit.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.bukkit.CoreConstants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AdminMessagePacket implements Packet {

    private final List<String> messages = new ArrayList<>();

    public AdminMessagePacket(String message) {
        this.messages.add(message);
    }

    public AdminMessagePacket(List<String> messages) {
        this.messages.addAll(messages);
    }

    @Override
    public void receive() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission(CoreConstants.ADMIN_PERM)) continue;

            this.messages.forEach(s -> player.sendMessage(CC.translate(s)));
        }

        this.messages.forEach(s -> Bukkit.getConsoleSender().sendMessage(CC.translate(s)));
    }

}
