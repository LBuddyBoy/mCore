package dev.minechase.core.rest.repository;

import dev.minechase.core.rest.model.PostCategory;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/20/2025
 */

@Repository
public interface PostCategoryRepository extends MongoRepository<PostCategory, String> {



}
