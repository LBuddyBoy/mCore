package dev.minechase.core.bukkit.task;

import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.server.model.QueuePlayer;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.velocity.packet.PlayerSendToServerPacket;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class QueuePlayerTask extends BukkitRunnable {

    private static int PAYLOAD = 3;
    private static long PAYLOAD_DELAY = 1_500L;
    private static long PAYLOAD_LAST_SENT = -1L;

    public QueuePlayerTask() {
        this.runTaskTimerAsynchronously(CorePlugin.getInstance(), 20, 20);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            QueuePlayer queuePlayer = CorePlugin.getInstance().getServerHandler().getQueuePlayer(player.getUniqueId());
            if (queuePlayer == null) continue;

            CoreServer server = queuePlayer.getServer();
            if (server == null) continue;

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(
                    CC.translate("<blend:&2;&a>You are currently #" + queuePlayer.getPosition() + " out of " + server.getQueuePlayers().size() + " players in the " + server.getName() + " queue!</>")
            ));
        }

        CoreServer server = CorePlugin.getInstance().getServerHandler().getLocalServer();

        if (server.isHub()) return;

        List<QueuePlayer> players = server.getSortedQueuePlayers();

        if (players.isEmpty()) return;
        if (server.isOffline() || server.isQueuePaused() || server.isWhitelisted()) return;
        if (PAYLOAD_LAST_SENT + PAYLOAD_DELAY > System.currentTimeMillis()) return;

        List<UUID> toRemove = new CopyOnWriteArrayList<>();

        for (int index = 0; index < PAYLOAD; index++) {
            if (index >= players.size()) continue;

            QueuePlayer queuePlayer = players.get(index);

            new PlayerSendToServerPacket(queuePlayer.getPlayerUUID(), server.getName()).send();

            toRemove.add(queuePlayer.getPlayerUUID());
        }

        if (!toRemove.isEmpty()) {
            toRemove.forEach(CorePlugin.getInstance().getServerHandler()::removeFromQueue);
            toRemove.clear();
        }

        PAYLOAD_LAST_SENT = System.currentTimeMillis();

    }

}
