package dev.minechase.core.rest.controller.forums;

import dev.minechase.core.rest.model.PostCategory;
import dev.minechase.core.rest.model.dto.PostCategoryDTO;
import dev.minechase.core.rest.service.forums.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/21/2025
 */

@RestController
@RequestMapping("/api/forums/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<PostCategoryDTO> categories() {
        return this.categoryService.getCategories().stream().map(PostCategoryDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostCategoryDTO> getById(@PathVariable String id) {
        PostCategory category = this.categoryService.getCategoryById(id);

        if (category == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(new PostCategoryDTO(category));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<CategoryStatsResponse> stats(@PathVariable String id) {
        PostCategory category = this.categoryService.getCategoryById(id);

        if (category == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(new CategoryStatsResponse(
                this.categoryService.getPostCount(category),
                this.categoryService.getReplyCount(category)
        ));
    }

    public record CategoryStatsResponse(int posts, int replies) {
    }

}