package dev.minechase.core.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.velocity.CoreVelocity;

public class UserListener {

    @Subscribe
    public void onLogin(LoginEvent event) {
        Player player = event.getPlayer();
        User user = CoreVelocity.getInstance().getUserHandler().loadUser(player.getUniqueId(), player.getUsername());

        CoreVelocity.getInstance().getUserHandler().getUsers().put(player.getUniqueId(), user);
    }

}
