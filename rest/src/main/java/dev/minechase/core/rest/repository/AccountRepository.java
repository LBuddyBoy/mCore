package dev.minechase.core.rest.repository;

import dev.minechase.core.rest.model.Account;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/15/2025
 */

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {

    Account findByEmail(String email);
    Account findByUsername(String username);
    Account findByMinecraftUUID(UUID minecraftUUID);

}
