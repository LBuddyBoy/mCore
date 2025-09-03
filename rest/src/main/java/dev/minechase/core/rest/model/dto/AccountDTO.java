package dev.minechase.core.rest.model.dto;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.rest.model.Account;
import lombok.Getter;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/18/2025
 */

@Getter
public class AccountDTO {

    private final String id;

    private final String username;
    private final String email;
    private final UUID minecraftUUID;
    private final String minecraftUsername;
    private final long createdAt;
    private UserDTO user;
    private String avatarUrl;

    public AccountDTO(Account account) {
        this.id = account.getId().toHexString();
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.avatarUrl = account.getAvatarUrl();
        this.minecraftUUID = account.getMinecraftUUID();
        this.minecraftUsername = UUIDUtils.getName(this.minecraftUUID);
        this.createdAt = account.getCreatedAt();

        if (this.avatarUrl.equals("https://www.gravatar.com/avatar/?d=mp&s=64") && this.minecraftUUID != null) {
            this.avatarUrl = "https://mc-heads.net/avatar/" + this.minecraftUUID + "/64";
        }

        if (this.minecraftUUID != null) {
            try {
                User user = CoreAPI.getInstance().getUserHandler().getOrCreateAsync(this.minecraftUUID).get(3, TimeUnit.SECONDS);

                this.user = new UserDTO(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
