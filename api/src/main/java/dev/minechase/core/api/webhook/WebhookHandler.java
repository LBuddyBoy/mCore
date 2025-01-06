package dev.minechase.core.api.webhook;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.webhook.model.IDiscordWebhook;
import dev.minechase.core.api.webhook.model.impl.GrantWebhook;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class WebhookHandler implements IModule {

    private final Map<String, IDiscordWebhook> webhooks;

    public WebhookHandler() {
        this.webhooks = new HashMap<>();
    }

    @Override
    public void load() {
        this.registerWebhook(new GrantWebhook());
    }

    @Override
    public void unload() {

    }

    public <T extends IDiscordWebhook> T getWebhook(Class<T> clazz) {
        return (T) this.webhooks.values().stream().filter(webhook -> webhook.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public void registerWebhook(IDiscordWebhook webhook) {
        if (this.webhooks.containsKey(webhook.getId())) return;

        this.webhooks.put(webhook.getId(), webhook);
        CoreAPI.getInstance().getLogger().info("[Webhook Handler] Loading " + webhook.getId() + " webhook. (" + webhook.getURL() + ")");
    }

}
