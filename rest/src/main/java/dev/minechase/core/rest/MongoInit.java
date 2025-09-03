package dev.minechase.core.rest;

import dev.minechase.core.rest.model.Account;
import dev.minechase.core.rest.model.Post;
import dev.minechase.core.rest.model.PostCategory;
import dev.minechase.core.rest.model.PostReply;
import dev.minechase.core.rest.service.AccountService;
import dev.minechase.core.rest.service.forums.CategoryService;
import dev.minechase.core.rest.service.forums.PostService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MongoInit {

    @Bean
    CommandLineRunner init(AccountService accountService, PostService postService, CategoryService categoryService, MongoTemplate mongoTemplate) {
        return args -> {
//            if (mongoTemplate.collectionExists("Accounts")) {
//                mongoTemplate.dropCollection("Accounts");
//            }
//
//            seedAccounts(accountService);
//            seedForums(mongoTemplate, categoryService);
//            seedPosts(mongoTemplate, accountService, categoryService, postService);
        };
    }

    public void seedAccounts(AccountService accountService) {
        accountService.createAccount(new Account("EthanToups", "ethantoups05@gmail.com", "test123"));
        accountService.createAccount(new Account("JohnWill", "jwill@gmail.com", "test123"));
        accountService.createAccount(new Account("GregSmith", "gsmith@gmail.com", "test123"));
        accountService.createAccount(new Account("JimCarry", "jcarry@gmail.com", "test123"));
    }

    public void seedForums(MongoTemplate mongoTemplate, CategoryService categoryService) {
        if (mongoTemplate.collectionExists("PostCategories")) {
            mongoTemplate.dropCollection("PostCategories");
        }

        categoryService.createCategory(new PostCategory(
                "News & Announcements",
                "Find all the latest news on the network!"
        ));
        categoryService.createCategory(new PostCategory(
                "Information & Changes",
                "Find all the latest information and changes on the network!"
        ));
    }

    public void seedPosts(MongoTemplate mongoTemplate, AccountService accountService, CategoryService categoryService, PostService postService) {
        if (mongoTemplate.collectionExists("Posts")) {
            mongoTemplate.dropCollection("Posts");
        }
        if (mongoTemplate.collectionExists("PostReplies")) {
            mongoTemplate.dropCollection("PostReplies");
        }

        List<Account> accounts = accountService.getAccounts();
        List<PostCategory> categories = categoryService.getCategories();

        for (int i = 0; i < 10; i++) {
            Account account = accounts.get(ThreadLocalRandom.current().nextInt(accounts.size()));
            PostCategory category = categories.get(ThreadLocalRandom.current().nextInt(categories.size()));

            postService.createPost(new Post(
                    "Test Title #" + i,
                    "Test message content for posts",
                    category.getId(),
                    account
            ));
        }

        List<Post> posts = postService.getAllPosts();

        for (int i = 0; i < 50; i++) {
            Post post = posts.get(ThreadLocalRandom.current().nextInt(posts.size()));
            Account account = accounts.get(ThreadLocalRandom.current().nextInt(accounts.size()));

            postService.saveReply(new PostReply(
                    account,
                    post.getId(),
                    "Test Message"
            ));
        }
    }
}
