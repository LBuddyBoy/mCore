package dev.minechase.core.api.punishment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PunishmentType {

    BLACKLIST("blacklisted"),
    BAN("banned"),
    MUTE("muted"),
    KICK("kicked"),
    WARN("warned");

    private final String plural;

}
