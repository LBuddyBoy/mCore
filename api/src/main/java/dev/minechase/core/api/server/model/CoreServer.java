package dev.minechase.core.api.server.model;

import com.google.common.collect.ImmutableList;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.Documented;
import dev.minechase.core.api.server.packet.ServerRebootPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class CoreServer extends Documented {

    private final String name;
    private String host;
    private int port, playerCount, maxPlayers;
    private long startedAt, stoppedAt;
    private boolean whitelisted, queuePaused;
    private List<String> groups = new ArrayList<>();
    private List<UUID> players = new ArrayList<>();

    public CoreServer(Document document) {
        this.name = document.getString("name");
        this.host = document.getString("host");
        this.port = document.getInteger("port");
        this.playerCount = document.getInteger("playerCount");
        this.maxPlayers = document.getInteger("maxPlayers");
        this.startedAt = document.getLong("startedAt");
        this.stoppedAt = document.getLong("stoppedAt");
        this.whitelisted = document.getBoolean("whitelisted");
        this.queuePaused = document.getBoolean("queuePaused");
        this.players = new ArrayList<>(document.getList("players", String.class, new ArrayList<>()).stream().map(UUID::fromString).toList());
        this.groups = new ArrayList<>(document.getList("groups", String.class, new ArrayList<>()));
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("name", this.name)
                .append("host", this.host)
                .append("port", this.port)
                .append("whitelisted", this.whitelisted)
                .append("queuePaused", this.queuePaused)
                .append("playerCount", this.playerCount)
                .append("maxPlayers", this.maxPlayers)
                .append("startedAt", this.startedAt)
                .append("stoppedAt", this.stoppedAt)
                .append("groups", this.groups)
                .append("players", this.players.stream().map(UUID::toString).toList());
    }

    public boolean isTemplate() {
        return this.name.startsWith("template-");
    }

    public boolean isInstance() {
        return this.name.startsWith("mini-");
    }

    public void updatePositions() {
        int position = 1;

        for (QueuePlayer player : this.getSortedQueuePlayers()) {
            player.setPosition(position++);
        }

    }

    public List<QueuePlayer> getQueuePlayers() {
        return CoreAPI.getInstance().getServerHandler().getQueuePlayers(this);
    }

    public List<QueuePlayer> getSortedQueuePlayers() {
        return this.getQueuePlayers().stream().sorted(Comparator.comparingInt(QueuePlayer::getPriority)).toList();
    }

    public int getQueueSize() {
        return this.getQueuePlayers().size();
    }

    public boolean isOnline() {
        return this.startedAt > 0 && this.stoppedAt <= 0;
    }

    public boolean isOffline() {
        return this.stoppedAt > 0;
    }

    public boolean isHub() {
        return this.name.toLowerCase().startsWith("hub-");
    }

    public ServerStatus getStatus() {
        return isOffline() ? ServerStatus.OFFLINE : isQueuePaused() ? ServerStatus.PAUSED : isWhitelisted() ? ServerStatus.WHITELISTED : ServerStatus.ONLINE;
    }

    public void markOnline() {
        this.startedAt = System.currentTimeMillis();
        this.stoppedAt = 0L;
        this.playerCount = 0;
        this.players.clear();
    }

    public void markOffline() {
        this.stoppedAt = System.currentTimeMillis();
        this.startedAt = 0L;
        this.playerCount = 0;
        this.players.clear();
    }

    public void reboot() {
        new ServerRebootPacket(this.name).send();
    }

}
