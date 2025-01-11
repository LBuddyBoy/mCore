package dev.minechase.core.velocity.motd.impl;

import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import dev.minechase.core.velocity.CoreLocale;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.motd.IMOTD;
import dev.minechase.core.velocity.util.CC;
import net.md_5.bungee.api.ChatColor;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LBuddyBoy Development
 * @file dev.minechase.core.velocity.motd.impl
 * @since 2/16/2024
 */
public class LockdownMOTD implements IMOTD {

    @Override
    public boolean isActive() {
        return CoreVelocity.getInstance().getLockdownHandler().isActive();
    }

    @Override
    public int getWeight() {
        return 100;
    }

    @Override
    public ServerPing.Builder generate(ProxyPingEvent event) {
        final ServerPing.Builder pong = event.getPing().asBuilder();

        pong.description(
                CC.translate(CoreLocale.LOCK_DOWN_MOTD_1.getString())
                .appendNewline()
                .append(CC.translate(CoreLocale.LOCK_DOWN_MOTD_2.getString()))
        );

        if (event.getConnection().getProtocolVersion().getProtocol() <= 340) {
            pong.description(
                    CC.translate(CoreLocale.LOCK_DOWN_LEGACY_MOTD_1.getString())
                            .appendNewline()
                            .append(CC.translate(CoreLocale.LOCK_DOWN_LEGACY_MOTD_2.getString()))
            );
        }

        try {
            pong.favicon(Favicon.create(ImageIO.read(new File(CoreLocale.LOCK_DOWN_FAVICON.getString()))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        pong.version(new ServerPing.Version(-1000, ChatColor.DARK_RED + "Maintenance"));

        return pong;
    }

}
