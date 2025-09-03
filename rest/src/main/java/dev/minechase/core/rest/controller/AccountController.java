package dev.minechase.core.rest.controller;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.grant.model.Grant;
import dev.minechase.core.api.sync.model.SyncCode;
import dev.minechase.core.api.sync.model.SyncInformation;
import dev.minechase.core.api.sync.model.WebsiteSyncInformation;
import dev.minechase.core.api.sync.packet.discord.DiscordSyncCodeDeletePacket;
import dev.minechase.core.api.sync.packet.discord.DiscordSyncInformationUpdatePacket;
import dev.minechase.core.api.sync.packet.discord.UserDiscordSyncPacket;
import dev.minechase.core.api.sync.packet.website.WebsiteSyncCodeDeletePacket;
import dev.minechase.core.api.sync.packet.website.WebsiteSyncInformationUpdatePacket;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.packet.PlayerMessagePacket;
import dev.minechase.core.rest.model.Account;
import dev.minechase.core.rest.model.dto.AccountDTO;
import dev.minechase.core.rest.model.dto.UserDTO;
import dev.minechase.core.rest.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/15/2025
 */

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable String id) {
        return ResponseEntity.ok(this.accountService.getAccountById(id).toDTO());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> getAccountById(@PathVariable String id, @RequestBody AccountUpdateRequest request) {
        Account account = this.accountService.getAccountById(id);

        if (account == null) return ResponseEntity.notFound().build();

        if (request.username != null && !request.username.equals(account.getUsername())) {
            if (this.accountService.getAccountByUsername(request.username()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("An account with that username already exists.");
            }
            account.setUsername(request.username);
        }

        if (request.avatarUrl != null && !request.avatarUrl.equals(account.getAvatarUrl())) {
            account.setAvatarUrl(request.avatarUrl);
        }

        if (request.email != null && !request.email.equals(account.getEmail())) {
            account.setEmail(request.email);
        }

        this.accountService.saveAccount(account);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<StatsResponse> stats(@PathVariable String id) {
        return ResponseEntity.ok(new StatsResponse(
                this.accountService.getReplyCount(id),
                this.accountService.getPostCount(id)
        ));
    }

    public record StatsResponse(int messages, int posts) {
    }

    public record AccountUpdateRequest(String username, String email, String avatarUrl) {
    }

}
