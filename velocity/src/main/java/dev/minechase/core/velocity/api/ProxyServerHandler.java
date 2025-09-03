package dev.minechase.core.velocity.api;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import dev.minechase.core.api.server.ServerHandler;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.util.CC;
import net.kyori.adventure.text.Component;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

public class ProxyServerHandler extends ServerHandler {

    @Override
    public void load() {
        super.load();

        for (CoreServer server : this.getServers().values()) {
            this.registerServer(server);
        }
    }

    @Override
    public void createLocalServer() {

    }

    @Override
    public void updateServer(CoreServer server) {
        super.updateServer(server);

        this.registerServer(server);
    }

    @Override
    public void unregisterServer(CoreServer server) {
        super.unregisterServer(server);

        CoreVelocity.getInstance().getProxy().getServer(server.getName()).ifPresent(registeredServer -> {
            CoreVelocity.getInstance().getProxy().unregisterServer(registeredServer.getServerInfo());
        });
    }

    public Optional<RegisteredServer> getRegisteredServer(CoreServer server) {
        return CoreVelocity.getInstance().getProxy().getServer(server.getName());
    }

    public void registerServer(CoreServer server) {
        if (server.getName().equalsIgnoreCase("JDA")) return;
        if (server.getName().equalsIgnoreCase("Proxy")) return;
        if (server.getName().equalsIgnoreCase("Website")) return;
        if (server.getName().equalsIgnoreCase("API")) return;

        CoreVelocity.getInstance().getProxy().getServer(server.getName()).ifPresentOrElse(registeredServer -> {
            ServerInfo info = registeredServer.getServerInfo();

            if (info.getAddress().getPort() != server.getPort() || !info.getAddress().getHostName().equals(server.getHost())) {
                CoreVelocity.getInstance().getProxy().unregisterServer(info);

                this.registerServer(server);
            }

        }, () -> {
            CoreVelocity.getInstance().getProxy().registerServer(new ServerInfo(
                    server.getName(),
                    new InetSocketAddress(server.getHost(), server.getPort())
            ));

            CoreVelocity.getInstance().getLogger().info("Creating a new registered server '" + server.getName() + "' on " + server.getHost() + ":" + server.getPort());
        });
    }

    public void sendPlayerToServer(UUID playerUUID, String serverName) {
        CoreVelocity.getInstance().getProxy().getPlayer(playerUUID).ifPresentOrElse(player -> {
            CoreVelocity.getInstance().getProxy().getServer(serverName).ifPresentOrElse(server -> {
                player.createConnectionRequest(server).connect().whenCompleteAsync(((result, throwable) -> {
                    if (result.isSuccessful()) return;

                    if (throwable != null) {
                        throwable.printStackTrace();
                    }

                    player.sendMessage(CC.translate("&cYou were kicked from " + serverName + ": " + CC.translate(result.getReasonComponent().orElse(Component.text("None")))));

                }));
            }, () -> CoreVelocity.getInstance().getLogger().warning("Tried sending " + UUIDUtils.getName(playerUUID) + ", but the server is not registered."));
        }, () -> CoreVelocity.getInstance().getLogger().warning("Tried sending " + UUIDUtils.getName(playerUUID) + ", but they were not connected to the proxy."));
    }

}
