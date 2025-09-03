package dev.minechase.core.rest.repository;

import dev.minechase.core.rest.model.Account;
import dev.minechase.core.rest.model.Post;
import dev.minechase.core.rest.model.PostCategory;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/20/2025
 */

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    Page<Post> findAll(Pageable pageable);
    Page<Post> findByCategoryId(ObjectId categoryId, Pageable pageable); // optional filter
    Page<Post> findByCreatedBy(Account createdBy, Pageable pageable);

}
