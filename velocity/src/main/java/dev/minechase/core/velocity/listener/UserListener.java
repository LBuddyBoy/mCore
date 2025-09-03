package dev.minechase.core.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.log.model.impl.NewUserLog;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.velocity.CoreConstants;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.util.CC;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackInfoLike;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class UserListener {

    @Subscribe
    public void onLogin(LoginEvent event) {
        Player player = event.getPlayer();
        User user = CoreVelocity.getInstance().getUserHandler().loadUser(player.getUniqueId(), player.getUsername());

        if (user.getCurrentIpAddress() == null) {
            user.setCurrentIpAddress(player.getRemoteAddress().getAddress().getHostAddress());
        }

        if (!user.hasPlayedBefore()) {
            user.setFirstJoinAt(System.currentTimeMillis());
            new NewUserLog(player.getUniqueId()).createLog();
        }

        CoreVelocity.getInstance().getUserHandler().getCache().save(player.getUniqueId(), user.toDocument().toJson());

        CoreVelocity.getInstance().getUserHandler().getUsers().put(player.getUniqueId(), user);

        if (!player.hasPermission("core.staff")) return;

        CoreConstants.broadcastToStaff(
                CC.translate("&9[Staff] " + user.getDisplayName() + "&a connected to the network"),
                100L
        );

//        try {
//            player.sendResourcePacks(ResourcePackRequest.resourcePackRequest()
//                    .packs(ResourcePackInfo.resourcePackInfo(
//                            UUID.randomUUID(), //
//                            new URI(""),
//                            "" // hash
//                    ))
//                    .prompt(CC.translate("Test pack"))
//                    .required(true)
//                    .build()
//            );
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Subscribe(priority = Short.MAX_VALUE)
    public void onSwitch(ServerConnectedEvent event) {
        RegisteredServer previousServer = event.getPreviousServer().orElse(null);
        RegisteredServer newServer = event.getServer();
        Player player = event.getPlayer();

        if (!player.hasPermission("core.staff")) return;

        User user = CoreVelocity.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (previousServer == null || newServer == null || user == null) return;

        String fromServer = previousServer.getServerInfo().getName();
        String toServer = newServer.getServerInfo().getName();

        CoreConstants.broadcastToStaff(
                CC.translate("&9[Staff] " + user.getDisplayName() + "&e switched from &7" + fromServer + " &eto &a" + toServer),
                100L
        );
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();

        User user = CoreVelocity.getInstance().getUserHandler().getUser(player.getUniqueId());

        user.save(true);

        CoreVelocity.getInstance().getUserHandler().getCache().delete(player.getUniqueId());
        CoreVelocity.getInstance().getUserHandler().getUsers().remove(player.getUniqueId());

        if (!player.hasPermission("core.staff")) return;

        CoreConstants.broadcastToStaff(
                CC.translate("&9[Staff] " + user.getDisplayName() + "&c disconnected from the network"),
                100L
        );
    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();
        RegisteredServer targetServer = event.getOriginalServer();

        User user = CoreAPI.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (user == null) {
            return; // fail-safe
        }

        // async save
        CompletableFuture.runAsync(() -> {
            CoreAPI.getInstance().getUserHandler().saveUser(user);
            CoreAPI.getInstance().getUserHandler().getCache().save(player.getUniqueId(), user.toDocument().toJson());
        }, CoreAPI.POOL).whenComplete((v, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                player.disconnect(Component.text("Error saving your profile."));
            }
        });

        // just allow the original connection to proceed
        event.setResult(ServerPreConnectEvent.ServerResult.allowed(targetServer));
    }

}
