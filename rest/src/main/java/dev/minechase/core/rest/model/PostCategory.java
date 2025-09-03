package dev.minechase.core.rest.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/20/2025
 */

@Document(collection = "PostCategories")
@Data
@NoArgsConstructor
public class PostCategory {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String title;

    private String description;

    public PostCategory(String title, String description) {
        this.title = title;
        this.description = description;
    }

}
