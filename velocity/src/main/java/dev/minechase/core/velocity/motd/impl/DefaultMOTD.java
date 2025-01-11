package dev.minechase.core.velocity.motd.impl;

import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.motd.IMOTD;
import dev.minechase.core.velocity.motd.model.MOTD;
import dev.minechase.core.velocity.util.CC;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LBuddyBoy Development
 * @file dev.minechase.core.velocity.motd.impl
 * @since 2/16/2024
 */
public class DefaultMOTD implements IMOTD {

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public int getWeight() {
        return 1000;
    }

    @Override
    public ServerPing.Builder generate(ProxyPingEvent event) {
        final ServerPing.Builder pong = event.getPing().asBuilder();

        MOTD activeMOTD = CoreVelocity.getInstance().getMotdHandler().getActiveMOTD();

        if (activeMOTD != null) {
            try {
                pong.favicon(activeMOTD.getFavicon());
            } catch (Exception e) {
                e.printStackTrace();
            }
            pong.description(CC.translate(activeMOTD.getLine1())
                    .appendNewline()
                    .append(CC.translate(activeMOTD.getLine2()))
            );

            if (event.getConnection().getProtocolVersion().getProtocol() <= 340) {
                pong.description(CC.translate(activeMOTD.getLegacyLine1())
                        .appendNewline()
                        .append(CC.translate(activeMOTD.getLegacyLine2()))
                );
            }
        }

        return pong;
    }

}
