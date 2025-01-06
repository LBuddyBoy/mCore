package dev.minechase.core.api.webhook.model;

import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.minechase.core.api.util.DiscordWebHookUtil;

import java.util.List;

public interface IDiscordWebhook {

    String getId();
    String getURL();
    String getHexColor();

    default void send(String username, List<String> message) {
        DiscordWebHookUtil.sendWebhookMessage(
                this.getURL(),
                username,
                StringUtils.join(message, "\n"),
                Integer.parseInt("0x" + this.getHexColor()),
                username
        );
    }

}
