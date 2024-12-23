package dev.minechase.core.api.api;

import dev.minechase.core.api.CoreAPI;

import java.util.List;

public interface IScoped {

    List<String> getScopes();

    default boolean isValid(String serverGroup) {
        return this.getScopes().stream().anyMatch(scope -> scope.equalsIgnoreCase(serverGroup));
    }

    default boolean isValidLocal() {
        return this.isValid(CoreAPI.getInstance().getServerGroup());
    }

}
