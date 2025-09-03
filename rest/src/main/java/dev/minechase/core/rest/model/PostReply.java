package dev.minechase.core.rest.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/21/2025
 */

@Document(collection = "PostReplies")
@NoArgsConstructor
@Data
public class PostReply {

    @Id
    private ObjectId id;

    @DBRef(lazy = true)
    private Account author;
    private ObjectId postId;
    private String message;
    private long createdAt, updatedAt;

    public PostReply(Account author, ObjectId postId, String message) {
        this.author = author;
        this.postId = postId;
        this.message = message;
        this.createdAt = System.currentTimeMillis();
    }

}
