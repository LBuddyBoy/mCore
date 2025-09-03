package dev.minechase.core.rest.model;

import dev.minechase.core.rest.model.dto.AuthDtos;
import dev.minechase.core.rest.model.dto.AccountDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/15/2025
 */

@Document(collection = "Accounts")
@Data
@NoArgsConstructor
public class Account {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;
    private String avatarUrl;
    private UUID minecraftUUID;
    private long createdAt;

    public Account(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.avatarUrl = "https://www.gravatar.com/avatar/?d=mp&s=64";
        this.minecraftUUID = null;
        this.createdAt = System.currentTimeMillis();
    }

    public Account(AuthDtos.RegisterRequest request) {
        this.username = request.username;
        this.email = request.email;
        this.password = request.password;
        this.avatarUrl = "https://www.gravatar.com/avatar/?d=mp&s=64";
        this.minecraftUUID = null;
        this.createdAt = System.currentTimeMillis();
    }

    public boolean isSynced() {
        return this.minecraftUUID != null;
    }

    public AccountDTO toDTO() {
        return new AccountDTO(this);
    }
}
