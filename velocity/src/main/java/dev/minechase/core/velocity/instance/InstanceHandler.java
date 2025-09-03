package dev.minechase.core.velocity.instance;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.instance.impl.SkyblockInstance;
import dev.minechase.core.velocity.instance.model.InstanceType;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/23/2025
 */

@Getter
public class InstanceHandler implements IModule {

    private final Map<String, InstanceType> types;

    public InstanceHandler() {
        this.types = new HashMap<>();
    }

    @Override
    public void load() {
        this.types.put("skyblock", new SkyblockInstance());
    }

    @Override
    public void unload() {

    }

    public int findAvailablePort() {
        Collection<CoreServer> servers = CoreVelocity.getInstance().getServerHandler().getServers().values();
        int port = 25566;
        for (CoreServer server : servers) {
            if (port >= server.getPort()) continue;

            port = server.getPort();
        }

        return ++port;
    }

}
