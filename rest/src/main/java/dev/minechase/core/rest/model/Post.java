package dev.minechase.core.rest.model;

import lombok.AllArgsConstructor;
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

@Document(collection = "Posts")
@NoArgsConstructor
@Data
public class Post {

    @Id
    private ObjectId id;

    private String title;
    private String content;

    private ObjectId categoryId;
    @DBRef(lazy = true)
    private Account createdBy;
    private long createdAt;

    public Post(String title, String content, ObjectId categoryId, Account createdBy) {
        this.title = title;
        this.content = content;
        this.categoryId = categoryId;
        this.createdBy = createdBy;
        this.createdAt = System.currentTimeMillis();
    }

}
