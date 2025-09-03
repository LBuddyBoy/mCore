package dev.minechase.core.rest.model.dto;

import dev.minechase.core.rest.model.Post;
import dev.minechase.core.rest.model.PostCategory;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/20/2025
 */

@NoArgsConstructor
@Data
public class PostCategoryDTO {

    @Id
    private String id;

    private String title;
    private String description;

    public PostCategoryDTO(PostCategory category) {
        this.id = category.getId().toHexString();
        this.title = category.getTitle();
        this.description = category.getDescription();
    }

}
