package dev.minechase.core.rest.controller.forums;

import dev.minechase.core.rest.model.Account;
import dev.minechase.core.rest.model.PostReply;
import dev.minechase.core.rest.model.dto.PostReplyDTO;
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
@RequestMapping("/api/forums/replies")
@RequiredArgsConstructor
public class ReplyController {

    private final AccountService accountService;
    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<PostReplyDTO>> getReplies(
            Pageable pageable,
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String postId)
    {
        if (postId != null) {
            return ResponseEntity.ok(this.postService.getRepliesByPost(postId, pageable).map(PostReplyDTO::new));
        }
        if (accountId != null) {
            Account account = this.accountService.getAccountById(accountId);

            if (account == null) return ResponseEntity.badRequest().build();

            return ResponseEntity.ok(this.postService.getRepliesByAccount(account, pageable).map(PostReplyDTO::new));
        }

        return ResponseEntity.ok(this.postService.getReplies(pageable).map(PostReplyDTO::new));
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<String> deleteReply(@PathVariable String replyId) {
        try {
            this.postService.deleteReply(replyId);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{replyId}")
    public ResponseEntity<String> updateReply(@PathVariable String replyId, @RequestBody UpdateReplyRequest request) {
        PostReply reply = this.postService.getReplyById(replyId);

        if (reply == null) return ResponseEntity.notFound().build();

        reply.setMessage(request.message);
        reply.setUpdatedAt(System.currentTimeMillis());

        this.postService.saveReply(reply);

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<String> createReply(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateReplyRequest request) {
        Account account = this.accountService.getAccountByEmail(jwt.getSubject());

        if (account == null) return ResponseEntity.badRequest().body("You need to be logged in to do this.");

        PostReply reply = new PostReply(
                account,
                new ObjectId(request.postId),
                request.message
        );

        this.postService.saveReply(reply);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public record CreateReplyRequest(String postId, String message) {
    }

    public record UpdateReplyRequest(String message) {
    }

}
