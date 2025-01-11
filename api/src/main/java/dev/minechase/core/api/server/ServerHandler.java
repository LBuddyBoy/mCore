package dev.minechase.core.api.server;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.GlobalLogPacket;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.server.model.QueuePlayer;
import dev.minechase.core.api.server.packet.QueuePlayerRemovePacket;
import dev.minechase.core.api.server.packet.QueuePlayerUpdatePacket;
import dev.minechase.core.api.server.packet.ServerUpdatePacket;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public abstract class ServerHandler<T> implements IModule {

    private final Map<String, CoreServer> servers;
    private final Map<UUID, QueuePlayer> queuePlayers;
    private MongoCollection<Document> serversCollection, queuePlayersCollection;

    public ServerHandler() {
        this.servers = new HashMap<>();
        this.queuePlayers = new ConcurrentHashMap<>();
    }

    @Override
    public void load() {
        this.serversCollection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("Servers");
        this.queuePlayersCollection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("QueuePlayers");

        for (Document document : this.serversCollection.find()) {
            CoreServer server = new CoreServer(document);

            this.servers.put(server.getName(), server);
        }

        for (Document document : this.queuePlayersCollection.find()) {
            QueuePlayer player = new QueuePlayer(document);

            this.queuePlayers.put(player.getPlayerUUID(), player);
        }

        this.createLocalServer();
        if (this.getLocalServer() == null) return;
        this.getLocalServer().markOnline();
    }

    @Override
    public void unload() {
        CoreServer server = this.getLocalServer();
        if (server == null) return;

        server.markOffline();

        new ServerUpdatePacket(server).send();
    }

    public CoreServer getServer(String name) {
        return this.servers.getOrDefault(name, null);
    }

    public void createLocalServer() {
        if (this.servers.containsKey(CoreAPI.getInstance().getServerName())) return;

        CoreServer server = new CoreServer(CoreAPI.getInstance().getServerName());

        this.servers.put(server.getName(), server);
        CoreAPI.getInstance().updateLocalServer();

        new ServerUpdatePacket(server).send();
        new GlobalLogPacket(Arrays.asList(
                "================================",
                "New Server Created!",
                "Name: " + server.getName(),
                "Host: " + server.getHost(),
                "Port: " + server.getPort(),
                "Players: " + server.getPlayerCount() + "/" + server.getMaxPlayers(),
                "Status: " + server.getStatus().name(),
                "================================"
        )).send();
    }

    public CoreServer getLocalServer() {
        return this.servers.getOrDefault(CoreAPI.getInstance().getServerName(), null);
    }

    public void updateServer(CoreServer server) {
        server.updatePositions();
        this.servers.put(server.getName(), server);
    }

    public void deleteServer(CoreServer server) {
        this.serversCollection.deleteOne(Filters.eq("name", server.getName()));
    }

    public void saveServer(CoreServer server, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> saveServer(server, false), CoreAPI.POOL);
            return;
        }

        this.serversCollection.replaceOne(Filters.eq("name", server.getName()), server.toDocument(), new ReplaceOptions().upsert(true));
    }

    /*
    Queue Section
     */

    public void removeFromQueue(UUID playerUUID) {
        QueuePlayer player = this.getQueuePlayer(playerUUID);

        if (player == null) {
            return;
        }

        new QueuePlayerRemovePacket(player).send();
    }

    public void addToQueue(UUID playerUUID, CoreServer server) {
        User user = CoreAPI.getInstance().getUserHandler().getUser(playerUUID);
        QueuePlayer player = new QueuePlayer(
                playerUUID,
                UUIDUtils.getName(playerUUID),
                server.getName(),
                user.getRank().getWeight(),
                0L,
                1
        );

        new QueuePlayerUpdatePacket(player).send();
    }

    public boolean isQueued(UUID playerUUID) {
        return this.queuePlayers.containsKey(playerUUID);
    }

    public QueuePlayer getQueuePlayer(UUID playerUUID) {
        return this.queuePlayers.getOrDefault(playerUUID, null);
    }

    public List<QueuePlayer> getQueuePlayers() {
        return this.queuePlayers.values().stream().toList();
    }

    public List<QueuePlayer> getQueuePlayers(CoreServer server) {
        return this.getQueuePlayers().stream().filter(player -> player.getQueueName().equalsIgnoreCase(server.getName())).toList();
    }

    public void updateQueuePlayer(QueuePlayer player) {
        CoreServer server = player.getServer();

        if (server != null) server.updatePositions();

        this.queuePlayers.put(player.getPlayerUUID(), player);
    }

    public void removeQueuePlayer(QueuePlayer player) {
        CoreServer server = player.getServer();

        this.queuePlayers.remove(player.getPlayerUUID());

        if (server != null) server.updatePositions();
    }

    public void deleteQueuePlayer(QueuePlayer player, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> deleteQueuePlayer(player, false), CoreAPI.POOL);
            return;
        }

        this.queuePlayersCollection.deleteOne(Filters.eq("playerUUID", player.getPlayerUUID().toString()));
    }

    public void saveQueuePlayer(QueuePlayer player, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> saveQueuePlayer(player, false), CoreAPI.POOL);
            return;
        }

        this.queuePlayersCollection.replaceOne(Filters.eq("playerUUID", player.getPlayerUUID().toString()), player.toDocument(), new ReplaceOptions().upsert(true));
    }

}
