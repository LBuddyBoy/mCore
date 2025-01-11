package dev.minechase.core.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.util.CC;
import net.kyori.adventure.text.Component;

import java.util.concurrent.TimeUnit;

public class UserListener {

    @Subscribe
    public void onLogin(LoginEvent event) {
        Player player = event.getPlayer();
        User user = CoreVelocity.getInstance().getUserHandler().loadUser(player.getUniqueId(), player.getUsername());

        CoreVelocity.getInstance().getUserHandler().getUsers().put(player.getUniqueId(), user);

        if (!player.hasPermission("core.staff")) return;

        Component message = CC.translate("&9[Staff] " + user.getDisplayName() + "&a connected to the network.");

        CoreVelocity.getInstance().getProxy().getScheduler().buildTask(CoreVelocity.getInstance(), () -> {
            for (Player other : CoreVelocity.getInstance().getProxy().getAllPlayers()) {
                if (!other.hasPermission("core.staff")) continue;

                other.sendMessage(message);
            }
        }).delay(100L, TimeUnit.MILLISECONDS).schedule();

        CoreVelocity.getInstance().getProxy().getConsoleCommandSource().sendMessage(message);
    }

    @Subscribe(priority = Short.MAX_VALUE)
    public void onSwitch(ServerConnectedEvent event) {
        RegisteredServer previousServer = event.getPreviousServer().orElse(null);
        RegisteredServer newServer = event.getServer();
        Player player = event.getPlayer();

        if (!player.hasPermission("core.staff")) return;

        User user = CoreVelocity.getInstance().getUserHandler().getUser(player.getUniqueId());

        if (previousServer == null || newServer == null || user == null) return;

        Component message = CC.translate("&9[Staff] " + user.getDisplayName() + "&e switched from " + previousServer.getServerInfo().getName() + " to " + newServer.getServerInfo().getName() + ".");

        CoreVelocity.getInstance().getProxy().getScheduler().buildTask(CoreVelocity.getInstance(), () -> {
            for (Player other : CoreVelocity.getInstance().getProxy().getAllPlayers()) {
                if (!other.hasPermission("core.staff")) continue;

                other.sendMessage(message);
            }
        }).delay(100L, TimeUnit.MILLISECONDS).schedule();

        CoreVelocity.getInstance().getProxy().getConsoleCommandSource().sendMessage(message);
    }

    @Subscribe
    public void onLogin(DisconnectEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("core.staff")) return;

        User user = CoreVelocity.getInstance().getUserHandler().getUser(player.getUniqueId());

        CoreVelocity.getInstance().getUserHandler().getUsers().remove(player.getUniqueId());

        Component message = CC.translate("&9[Staff] " + user.getDisplayName() + "&c disconnected from the network.");

        CoreVelocity.getInstance().getProxy().getScheduler().buildTask(CoreVelocity.getInstance(), () -> {
            for (Player other : CoreVelocity.getInstance().getProxy().getAllPlayers()) {
                if (!other.hasPermission("core.staff")) continue;

                other.sendMessage(message);
            }
        }).delay(100L, TimeUnit.MILLISECONDS).schedule();

        CoreVelocity.getInstance().getProxy().getConsoleCommandSource().sendMessage(message);
    }

}
