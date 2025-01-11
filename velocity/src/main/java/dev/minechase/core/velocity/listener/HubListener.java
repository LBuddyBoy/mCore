package dev.minechase.core.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.util.CC;

import java.util.List;

public class HubListener {

    @Subscribe
    public void onConnect(ServerPreConnectEvent event) {
        RegisteredServer originalServer = event.getOriginalServer();
        RegisteredServer previousServer = event.getPreviousServer();
        Player player = event.getPlayer();

        /*
        Initial Proxy Connect
         */

        if (originalServer == null || previousServer == null) {
            List<CoreServer> hubs = CoreVelocity.getInstance().getHubs();

            if (hubs.isEmpty()) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                player.disconnect(CC.translate("<blend:&4;&c>[Fallback Error] We couldn't find a hub server to connect you to.</>"));
                return;
            }

            CoreServer server = hubs.getFirst();

            CoreVelocity.getInstance().getServerHandler().getRegisteredServer(server).ifPresentOrElse(registerServer -> {
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(registerServer));
            }, () -> {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                player.disconnect(CC.translate("<blend:&4;&c>[Fallback Error] We couldn't find a hub server to connect you to.</>"));
            });
            return;
        }

    }

}
