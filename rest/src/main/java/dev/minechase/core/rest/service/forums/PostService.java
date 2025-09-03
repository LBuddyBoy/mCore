package dev.minechase.core.rest.service.forums;

import dev.minechase.core.rest.model.Account;
import dev.minechase.core.rest.model.Post;
import dev.minechase.core.rest.model.PostReply;
import dev.minechase.core.rest.repository.PostReplyRepository;
import dev.minechase.core.rest.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/21/2025
 */

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostReplyRepository replyRepository;
    private final MongoTemplate mongoTemplate;

    public Post createPost(Post post) {
        return this.postRepository.save(post);
    }

    public void saveReply(PostReply reply) {
        this.replyRepository.save(reply);
    }

    public void deleteReply(String replyId) {
        this.replyRepository.deleteById(replyId);
    }

    public Post getPostById(String id) {
        return this.postRepository.findById(id).orElse(null);
    }

    public List<Post> getAllPosts() {
        return this.postRepository.findAll();
    }

    public Page<Post> getPosts(Pageable pageable) {
        return this.postRepository.findAll(pageable);
    }

    public Page<Post> getPostsByCategory(String categoryId, Pageable pageable) {
        return this.postRepository.findByCategoryId(new ObjectId(categoryId), pageable);
    }

    public Page<Post> getPostsByAccount(Account createdBy, Pageable pageable) {
        return this.postRepository.findByCreatedBy(createdBy, pageable);
    }

    public Page<PostReply> getReplies(Pageable pageable) {
        return this.replyRepository.findAll(pageable);
    }

    public PostReply getReplyById(String replyId) {
        return this.replyRepository.findById(replyId).orElse(null);
    }

    public Page<PostReply> getRepliesByPost(String postId, Pageable pageable) {
        return this.replyRepository.findByPostId(new ObjectId(postId), pageable);
    }

    public Page<PostReply> getRepliesByAccount(Account author, Pageable pageable) {
        return this.replyRepository.findByAuthor(author, pageable);
    }

    public int getReplyCount(Post post) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("postId").is(post.getId())),
                Aggregation.count().as("replyCount")
        );

        AggregationResults<Document> results =
                this.mongoTemplate.aggregate(agg, "PostReplies", Document.class);

        return results.getUniqueMappedResult() != null
                ? results.getUniqueMappedResult().getInteger("replyCount", 0)
                : 0;
    }

}
