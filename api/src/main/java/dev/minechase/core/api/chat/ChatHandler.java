package dev.minechase.core.api.chat;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.chat.model.ChatSettings;
import dev.minechase.core.api.user.model.User;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class ChatHandler implements IModule {

    private MongoCollection<Document> filterCollection, chatCollection;
    private List<String> filterList = new ArrayList<>();
    private ChatSettings localSettings;

    @Override
    public void load() {
        this.filterCollection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("Filter");
        this.chatCollection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("Chat");

        this.loadFilter();
        this.loadSettings();
    }

    @Override
    public void unload() {

    }

    private void loadSettings() {
        this.localSettings = this.getSettings(CoreAPI.getInstance().getServerName());
    }

    public void saveSettings(ChatSettings settings) {
        Bson filter = Filters.eq("server", settings.getServerName());

        this.chatCollection.replaceOne(filter, settings.toDocument(), new ReplaceOptions().upsert(true));
    }
    
    public ChatSettings getSettings(String serverName) {
        ChatSettings settings = null;
        Document document = this.chatCollection.find(Filters.eq("server", serverName)).first();

        if (document == null) {
            settings = new ChatSettings(serverName);
        } else {
            settings = new ChatSettings(document);
        }

        return settings;
    }

    protected void slowChat(int secondsDelay, long duration) {
        this.localSettings.setSlowDelay(secondsDelay);
        this.localSettings.setSlowDuration(duration);
        this.localSettings.setSlowedAt(System.currentTimeMillis());
        this.saveSettings(this.localSettings);
    }

    protected void unslowChat() {
        this.localSettings.setSlowDelay(0L);
        this.localSettings.setSlowDuration(0L);
        this.localSettings.setSlowedAt(0L);
        this.saveSettings(this.localSettings);
    }

    protected void mute(long duration) {
        this.localSettings.setMuteDuration(duration);
        this.localSettings.setMutedAt(System.currentTimeMillis());
        this.saveSettings(this.localSettings);
    }

    protected void unmute() {
        this.localSettings.setMuteDuration(0L);
        this.localSettings.setMutedAt(0L);
        this.saveSettings(this.localSettings);
    }

    private void loadFilter() {
        Document document = this.filterCollection.find(Filters.eq("settings", true)).first();

        if (document == null) {
            this.filterCollection.insertOne(new Document()
                    .append("settings", true)
                    .append("filter", new ArrayList<>(Arrays.asList(
                            "nigger",
                            "nigga",
                            "faggot",
                            "bean",
                            "beaner",
                            "gook",
                            "chink"
                    )))
            );
        } else {
            this.filterList.addAll(document.getList("filter", String.class, new ArrayList<>()));
        }
    }

    public void updateFilter(List<String> newFilter) {
        this.filterList = newFilter;
    }

    public void saveFilter(List<String> newFilter) {
        Document document = new Document();

        document.put("settings", true);
        document.put("filter", newFilter);

        this.filterCollection.replaceOne(Filters.eq("settings", true), document, new ReplaceOptions().upsert(true));
    }
    

}
