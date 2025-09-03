package dev.minechase.core.api.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServerStatus {

    OFFLINE("&c&lOFFLINE"),
    ONLINE("&a&lONLINE"),
    PAUSED("&b&lPAUSED"),
    WHITELISTED("&e&lWHITELISTED");

    private final String displayName;

}
