package dev.minechase.core.rest.service;

import dev.minechase.core.rest.model.Account;
import dev.minechase.core.rest.model.Post;
import dev.minechase.core.rest.model.dto.AuthDtos;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/15/2025
 */
public interface AccountService extends UserDetailsService {

    ResponseEntity<String> loginAccount(AuthDtos.LoginRequest request);
    void createAccount(Account account);
    Account saveAccount(Account account);
    Account getAccountByMinecraftUUID(UUID playerUUID);
    Account getAccountByEmail(String email);
    Account getAccountByUsername(String username);
    Account getAccountById(String id);
    List<Account> getAccounts();
    int getReplyCount(String accountId);
    int getPostCount(String accountId);
}
