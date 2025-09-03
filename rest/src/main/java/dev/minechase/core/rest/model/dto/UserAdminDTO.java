package dev.minechase.core.rest.model.dto;

import dev.minechase.core.api.grant.model.Grant;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.user.model.UserMetadata;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/20/2025
 */

@Getter
public class UserAdminDTO {

    private final UUID uniqueId;
    private final String name, currentIpAddress;
    private final long firstJoinAt;
    private final Grant activeGrant;
    private final UserMetadata persistentMetadata;
    private final UUID disguiseRank;
    private final String disguiseName, disguiseSkinTextures, disguiseSkinSignature;

    public UserAdminDTO(User user) {
        this.uniqueId = user.getUniqueId();
        this.name = user.getName();
        this.currentIpAddress = user.getCurrentIpAddress();
        this.firstJoinAt = user.getFirstJoinAt();
        this.activeGrant = user.getActiveGrant();
        this.persistentMetadata = user.getPersistentMetadata();
        this.disguiseRank = user.getDisguiseRank();
        this.disguiseName = user.getDisguiseName();
        this.disguiseSkinTextures = user.getDisguiseSkinTextures();
        this.disguiseSkinSignature = user.getDisguiseSkinSignature();
    }

}
