package dev.minechase.core.velocity.api;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import dev.minechase.core.api.server.ServerHandler;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.velocity.CoreVelocity;

import java.net.InetSocketAddress;
import java.util.Optional;

public class ProxyServerHandler extends ServerHandler<Player> {

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

    public Optional<RegisteredServer> getRegisteredServer(CoreServer server) {
        return CoreVelocity.getInstance().getProxy().getServer(server.getName());
    }

    public void registerServer(CoreServer server) {
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

}
