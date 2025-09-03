package dev.minechase.core.rest.controller.forums;

import dev.minechase.core.rest.model.Account;
import dev.minechase.core.rest.model.Post;
import dev.minechase.core.rest.model.PostReply;
import dev.minechase.core.rest.model.dto.PostDTO;
import dev.minechase.core.rest.service.AccountService;
import dev.minechase.core.rest.service.forums.PostService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/21/2025
 */

@RestController
@RequestMapping("/api/forums/posts")
@RequiredArgsConstructor
public class PostController {

    private final AccountService accountService;
    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<PostDTO>> getPosts(
            Pageable pageable,
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String categoryId)
    {
        if (categoryId != null) {
            return ResponseEntity.ok(this.postService.getPostsByCategory(categoryId, pageable).map(PostDTO::new));
        }
        if (accountId != null) {
            Account account = this.accountService.getAccountById(accountId);

            if (account == null) return ResponseEntity.badRequest().build();

            return ResponseEntity.ok(this.postService.getPostsByAccount(account, pageable).map(PostDTO::new));
        }

        return ResponseEntity.ok(this.postService.getPosts(pageable).map(PostDTO::new));
    }

    @PostMapping
    public ResponseEntity<String> createReply(@AuthenticationPrincipal Jwt jwt, @RequestBody CreatePostRequest request) {
        Account account = this.accountService.getAccountByEmail(jwt.getSubject());

        if (account == null) return ResponseEntity.badRequest().body("You need to be logged in to do this.");

        Post post = new Post(
                request.title,
                request.content,
                new ObjectId(request.categoryId),
                account
        );
        Post created = this.postService.createPost(post);

        return new ResponseEntity<>(created.getId().toHexString(), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable String id) {
        Post post = this.postService.getPostById(id);

        if (post == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(new PostDTO(post));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<PostStatsResponse> postStats(@PathVariable String id) {
        Post post = this.postService.getPostById(id);

        if (post == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(new PostStatsResponse(
                this.postService.getReplyCount(post),
                0
        ));
    }

    public record PostStatsResponse(int replies, int views) {
    }

    public record CreatePostRequest(String categoryId, String title, String content) {
    }

}
