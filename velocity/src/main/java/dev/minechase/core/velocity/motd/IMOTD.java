package dev.minechase.core.velocity.motd;

import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LBuddyBoy Development
 * @file dev.minechase.core.velocity.motd
 * @since 2/16/2024
 */
public interface IMOTD {

    boolean isActive();
    int getWeight(); // 0 being the first in line
    ServerPing.Builder generate(ProxyPingEvent event);

}
