package dev.minechase.core.rest.api;

import dev.minechase.core.api.sync.WebsiteSyncHandler;
import dev.minechase.core.api.sync.model.WebsiteSyncInformation;
import dev.minechase.core.rest.model.Account;
import dev.minechase.core.rest.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/19/2025
 */

@Component
@RequiredArgsConstructor
public class SpringSyncHandler extends WebsiteSyncHandler {

    private final AccountService accountService;

    @Override
    public void removeInfo(WebsiteSyncInformation info) {
        super.removeInfo(info);

        Account account = this.accountService.getAccountById(info.getWebsiteUserId());
        account.setMinecraftUUID(null);
        this.accountService.saveAccount(account);
    }
}
