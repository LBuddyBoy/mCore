package dev.minechase.core.rest.controller;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.sync.model.SyncCode;
import dev.minechase.core.api.sync.model.WebsiteSyncInformation;
import dev.minechase.core.api.sync.packet.website.WebsiteSyncCodeDeletePacket;
import dev.minechase.core.api.sync.packet.website.WebsiteSyncInformationUpdatePacket;
import dev.minechase.core.bukkit.packet.PlayerMessagePacket;
import dev.minechase.core.rest.model.Account;
import dev.minechase.core.rest.model.dto.AccountDTO;
import dev.minechase.core.rest.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/21/2025
 */

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<SyncResponse> sync(@AuthenticationPrincipal Jwt jwt, @RequestBody SyncRequest request) {
        SyncCode code = CoreAPI.getInstance().getWebsiteSyncHandler().getSyncCode(request.code);

        if (code == null) return ResponseEntity.status(404).body(new SyncResponse(false, "Code not found.", null));

        Account account = this.accountService.getAccountByEmail(jwt.getSubject());

        try {
            WebsiteSyncInformation information = CoreAPI.getInstance().getWebsiteSyncHandler().getSyncInformation(code.getPlayerUUID()).get(3, TimeUnit.SECONDS);

            if (information != null) {
                return ResponseEntity.status(409).body(new SyncResponse(false, "Already synced.", null));
            }

            information = new WebsiteSyncInformation(code.getPlayerUUID(), account.getId().toHexString());

            new WebsiteSyncCodeDeletePacket(code).send();
            new WebsiteSyncInformationUpdatePacket(information).send();
            new PlayerMessagePacket(Arrays.asList(
                    "&aYour account is now synced with " + account.getUsername()
            ), code.getPlayerUUID()).send();

            account.setMinecraftUUID(code.getPlayerUUID());
            accountService.saveAccount(account);

            return ResponseEntity.ok(new SyncResponse(true, "Synced successfully.", account.toDTO()));
        } catch (TimeoutException te) {
            return ResponseEntity.status(503).body(new SyncResponse(false, "Sync timed out. Try again.", null));
        } catch (ExecutionException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body(new SyncResponse(false, "Sync failed.", null));
        }
    }

    public record SyncRequest(Integer code) {
    }

    public record SyncResponse(boolean ok, String message, AccountDTO account) {
    }


}
