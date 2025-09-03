package dev.minechase.core.velocity;

import com.velocitypowered.api.proxy.Player;
import dev.minechase.core.velocity.util.CC;
import net.kyori.adventure.text.Component;

import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/23/2025
 */
public class CoreConstants {

    public static void broadcastToStaff(Component message) {
        broadcastToStaff(message, null);
    }

    public static void broadcastToStaff(Component component, Long delayMs) {
        if (delayMs == null) {
            for (Player other : CoreVelocity.getInstance().getProxy().getAllPlayers()) {
                if (!other.hasPermission("core.staff")) continue;

                other.sendMessage(component);
            }
            CoreVelocity.getInstance().getProxy().getConsoleCommandSource().sendMessage(component);
            return;
        }
        CoreVelocity.getInstance().getProxy().getScheduler().buildTask(CoreVelocity.getInstance(), () -> {
            broadcastToStaff(component, null);
        }).delay(delayMs, TimeUnit.MILLISECONDS).schedule();
    }

}
