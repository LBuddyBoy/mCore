package dev.minechase.core.rest.service;

import dev.minechase.core.rest.model.Account;
import dev.minechase.core.rest.model.dto.AuthDtos;
import dev.minechase.core.rest.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/15/2025
 */

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final MongoTemplate mongoTemplate;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = this.accountRepository.findByEmail(email);

        if (account == null) {
            log.info("User {} not found in the database.", email);
            throw new UsernameNotFoundException(email);
        } else {
            log.info("User {} was found in the database.", account.getEmail());
        }

        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(),
                Collections.emptyList()
        );
    }

    @Override
    public ResponseEntity<String> loginAccount(AuthDtos.LoginRequest request) {
        log.info("Attempting login for {} with password {}", request.email, request.password);

        Account account = this.getAccountByEmail(request.email);

        if (account == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        boolean matches = this.passwordEncoder.matches(request.password, account.getPassword());

        return !matches ? ResponseEntity.badRequest().body("Incorrect email or password.") : ResponseEntity.ok(tokenService.generateToken(account));
    }

    @Override
    public void createAccount(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        this.saveAccount(account);
    }

    @Override
    public Account saveAccount(Account account) {
        log.info("Saving new user {}", account.getEmail());
        return this.accountRepository.save(account);
    }

    @Override
    public Account getAccountByMinecraftUUID(UUID playerUUID) {
        return this.accountRepository.findByMinecraftUUID(playerUUID);
    }

    @Override
    public Account getAccountByEmail(String email) {
        log.info("Fetching user {}", email);
        return this.accountRepository.findByEmail(email);
    }

    @Override
    public Account getAccountByUsername(String username) {
        return this.accountRepository.findByUsername(username);
    }

    @Override
    public Account getAccountById(String id) {
        return this.accountRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(id));
    }

    @Override
    public List<Account> getAccounts() {
        log.info("Fetching all users");
        return this.accountRepository.findAll();
    }

    @Override
    public int getReplyCount(String accountId) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("author.$id").is(new ObjectId(accountId))),
                Aggregation.count().as("replyCount")
        );

        AggregationResults<Document> results =
                this.mongoTemplate.aggregate(agg, "PostReplies", Document.class);

        return results.getUniqueMappedResult() != null
                ? results.getUniqueMappedResult().getInteger("replyCount", 0)
                : 0;
    }

    @Override
    public int getPostCount(String accountId) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("createdBy.$id").is(new ObjectId(accountId))),
                Aggregation.count().as("postCount")
        );

        AggregationResults<Document> results =
                this.mongoTemplate.aggregate(agg, "Posts", Document.class);

        return results.getUniqueMappedResult() != null
                ? results.getUniqueMappedResult().getInteger("postCount", 0)
                : 0;
    }

}
