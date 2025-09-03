package dev.minechase.core.rest.model.dto;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.user.model.User;
import lombok.Getter;

import java.util.UUID;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/20/2025
 */

@Getter
public class UserDTO {

    private final UUID uniqueId;
    private final String name;
    private final long firstJoinAt;
    private final Rank rank;
    private final String currentServer;
    private final boolean online;

    public UserDTO(User user) {
        this.uniqueId = user.getUniqueId();
        this.name = user.getName();
        this.firstJoinAt = user.getFirstJoinAt();
        this.currentServer = CoreAPI.getInstance().getServerHandler().getPlayerLocationStorage().load(user.getUniqueId());
        this.online = this.currentServer != null;

        if (user.getActiveGrant() == null) {
            this.rank = CoreAPI.getInstance().getRankHandler().getDefaultRank();
            return;
        }

        this.rank = user.getActiveGrant().getRank();
    }

}
