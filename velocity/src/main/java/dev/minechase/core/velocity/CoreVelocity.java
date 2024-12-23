package dev.minechase.core.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(
        id = "mcore-velocity",
        name = "mcore-velocity",
        version = "1.0",
        authors = {"LBuddyBoy"},
        dependencies = {
                @Dependency(id = "commons")
        }
)

@Getter
public class CoreVelocity {

    @Getter private static CoreVelocity instance;

    private final ProxyServer proxy;
    private final java.util.logging.Logger logger;
    private final Path dataDirectory;

    @Inject
    public CoreVelocity(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
    }
}
