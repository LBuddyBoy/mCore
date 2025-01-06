package dev.minechase.core.bukkit.packet;

import dev.lbuddyboy.commons.api.redis.packet.Packet;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.bukkit.CoreConstants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GlobalMessagePacket implements Packet {

    private final List<String> messages = new ArrayList<>();

    public GlobalMessagePacket(String message) {
        this.messages.add(message);
    }

    public GlobalMessagePacket(List<String> messages) {
        this.messages.addAll(messages);
    }

    @Override
    public void receive() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.messages.forEach(player::sendMessage);
        }

        this.messages.forEach(s -> Bukkit.getConsoleSender().sendMessage(CC.translate(s)));
    }

}
