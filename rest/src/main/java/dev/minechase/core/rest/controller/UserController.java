package dev.minechase.core.rest.controller;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.rest.model.Account;
import dev.minechase.core.rest.model.dto.AccountDTO;
import dev.minechase.core.rest.model.dto.UserDTO;
import dev.minechase.core.rest.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/20/2025
 */

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AccountService accountService;

    @GetMapping("/{userId}")
    public ResponseEntity<GetUserResponse> getUserById(@PathVariable String userId) {
        try {
            UUID playerUUID = UUID.fromString(userId);

            try {
                User user = CoreAPI.getInstance().getUserHandler().getOrCreateAsync(playerUUID).get(3, TimeUnit.SECONDS);

                if (user == null) return ResponseEntity.notFound().build();

                Account account = this.accountService.getAccountByMinecraftUUID(user.getUniqueId());

                if (account == null) return ResponseEntity.notFound().build();

                return ResponseEntity.ok(new GetUserResponse(new UserDTO(user), account.toDTO()));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public record GetUserResponse(UserDTO user, AccountDTO account) {}

}
