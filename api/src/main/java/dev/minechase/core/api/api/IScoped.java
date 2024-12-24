package dev.minechase.core.api.api;

import dev.minechase.core.api.CoreAPI;

import java.util.List;

public interface IScoped {

    List<String> getScopes();

    default boolean isValid(List<String> serverGroups) {
        return this.getScopes().stream().anyMatch(scope -> serverGroups.stream().anyMatch(scope::equalsIgnoreCase));
    }

    default boolean isValidLocal() {
        return this.isValid(CoreAPI.getInstance().getServerGroups());
    }

}
