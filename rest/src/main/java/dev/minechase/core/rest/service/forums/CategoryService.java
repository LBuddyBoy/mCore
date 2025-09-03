package dev.minechase.core.rest.service.forums;

import dev.minechase.core.rest.model.PostCategory;
import dev.minechase.core.rest.repository.PostCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
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
public class CategoryService {

    private final PostCategoryRepository categoryRepository;
    private final MongoTemplate mongoTemplate;

    public void createCategory(PostCategory category) {
        this.categoryRepository.save(category);
    }

    public PostCategory getCategoryById(String id) {
        return this.categoryRepository.findById(id).orElse(null);
    }

    public List<PostCategory> getCategories() {
        return this.categoryRepository.findAll();
    }

    public int getReplyCount(PostCategory category) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.lookup("Posts", "postId", "_id", "post"),
                Aggregation.unwind("post"),
                Aggregation.match(Criteria.where("post.categoryId").is(category.getId())),
                Aggregation.count().as("replyCount")
        );

        AggregationResults<Document> results =
                this.mongoTemplate.aggregate(agg, "PostReplies", Document.class);

        return results.getUniqueMappedResult().getInteger("replyCount", 0);
    }

    public int getPostCount(PostCategory category) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("categoryId").is(category.getId())),
                Aggregation.count().as("postCount")
        );

        AggregationResults<Document> results =
                this.mongoTemplate.aggregate(agg, "Posts", Document.class);

        return results.getUniqueMappedResult() != null
                ? results.getUniqueMappedResult().getInteger("postCount", 0)
                : 0;
    }

}
