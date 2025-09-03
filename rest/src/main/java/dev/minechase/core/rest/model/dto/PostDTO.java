package dev.minechase.core.rest.model.dto;

import dev.minechase.core.rest.model.Account;
import dev.minechase.core.rest.model.Post;
import dev.minechase.core.rest.model.PostCategory;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/20/2025
 */

@NoArgsConstructor
@Data
public class PostDTO {

    private String id;

    private String title;
    private String content;
    private String categoryId;
    private AccountDTO createdBy;
    private long createdAt;

    public PostDTO(Post post) {
        this.id = post.getId().toHexString();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.categoryId = post.getCategoryId().toHexString();
        this.createdBy = post.getCreatedBy().toDTO();
        this.createdAt = post.getCreatedAt();
    }

}
