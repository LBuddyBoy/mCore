package dev.minechase.core.velocity.instance.model;

import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.velocity.CoreVelocity;

import java.util.List;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/23/2025
 */
public interface InstanceType {

    String getId();

    int createInstance(int amount);

    int deleteInstanceAmount(int amount);

    void deleteInstanceByName(String name);

    default boolean isInstance(String serverName) {
        return serverName.startsWith("mini-" + getId() + "-");
    }

    default String createServerName() {
        long idNum = this.countInstances() + 1;
        String idStr = (idNum < 10 ? "0" + idNum : String.valueOf(idNum));

        return "mini-" + getId() + "-" + idStr;
    }

    default boolean isInstance(CoreServer server) {
        return this.isInstance(server.getName());
    }

    default List<CoreServer> getInstances() {
        return CoreVelocity.getInstance().getServerHandler().getServers().values()
                .stream()
                .filter(this::isInstance)
                .toList();
    }

    default long countInstances() {
        return CoreVelocity.getInstance().getServerHandler().getServers().values()
                .stream()
                .filter(this::isInstance)
                .count();
    }

}
