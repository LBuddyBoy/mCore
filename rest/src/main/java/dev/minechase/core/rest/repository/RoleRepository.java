package dev.minechase.core.rest.repository;

import dev.minechase.core.rest.model.Profile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProfileRepository extends MongoRepository<Profile, ObjectId> {

    Optional<Profile> findByUsername(String username);
    Optional<Profile> findByEmail(String email);
    Optional<Profile> findByToken(String token);
    Optional<Profile> findByEmailVerifyToken(String emailVerifyToken);


}
