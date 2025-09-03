package dev.minechase.core.rest.model.dto;

import dev.minechase.core.rest.model.PostReply;
import lombok.Getter;
import org.bson.types.ObjectId;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/21/2025
 */

@Getter
public class PostReplyDTO {

    private final String id;
    private final String message;
    private final String postId;
    private final AccountDTO author;
    private final long createdAt, updatedAt;

    public PostReplyDTO(PostReply reply) {
        this.id = reply.getId().toHexString();
        this.message = reply.getMessage();
        this.postId = reply.getPostId().toHexString();
        this.author = reply.getAuthor().toDTO();
        this.createdAt = reply.getCreatedAt();
        this.updatedAt = reply.getUpdatedAt();
    }

}
