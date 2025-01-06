package dev.minechase.core.api.punishment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PunishmentType {

    BLACKLIST("Blacklist", "&6", "&e", "blacklisted"),
    BAN("Ban", "&9", "&b", "banned"),
    MUTE("Mute", "&4", "&c", "muted"),
    KICK("Kick", "&2", "&a", "kicked"),
    WARN("Warn", "&7", "&f", "warned");

    private final String displayName;
    private final String primaryColor, secondaryColor;
    private final String plural;

}
