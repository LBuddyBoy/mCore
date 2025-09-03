package dev.minechase.core.api.filter;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class ChatHandler implements IModule {

    private MongoCollection<Document> filterCollection, chatCollection;
    private List<String> filterList = new ArrayList<>();
    @Setter private long slowDelay, slowDuration, slowedAt;
    @Setter private long muteDuration, mutedAt;

    @Override
    public void load() {
        this.filterCollection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("Filter");
        this.chatCollection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("Chat");

        this.loadFilter();
        this.loadChat();
    }

    @Override
    public void unload() {

    }

    private void loadChat() {
        Document document = this.chatCollection.find(Filters.eq("server", CoreAPI.getInstance().getServerName())).first();

        if (document == null) {
            document = new Document()
                    .append("server", CoreAPI.getInstance().getServerName())
                    .append("slowDelay", 0L)
                    .append("slowDuration", 0L)
                    .append("slowedAt", 0L)
                    .append("muteDuration", 0L)
                    .append("mutedAt", 0L);

            this.chatCollection.insertOne(document);
        }

        this.slowDelay = document.getLong("slowDelay");
        this.slowDuration = document.getLong("slowDuration");
        this.slowedAt = document.getLong("slowedAt");
        this.muteDuration = document.getLong("muteDuration");
        this.mutedAt = document.getLong("mutedAt");
    }

    public void saveChat() {
        Bson filter = Filters.eq("server", CoreAPI.getInstance().getServerName());
        Document document = this.chatCollection.find(filter).first();

        if (document == null) {
            document = new Document();
        }

        document.put("server", CoreAPI.getInstance().getServerName());
        document.put("slowDelay", this.slowDelay);
        document.put("slowDuration", this.slowDuration);
        document.put("slowedAt", this.slowedAt);
        document.put("muteDuration", this.muteDuration);
        document.put("mutedAt", this.mutedAt);

        this.chatCollection.replaceOne(filter, document, new ReplaceOptions().upsert(true));
    }

    public boolean isSlowed() {
        return this.slowDelay > 0 && 
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
        Document document = this.filterCollection.find(Filters.eq("settings", true)).first();

        document.put("filter", newFilter);

        this.filterCollection.replaceOne(Filters.eq("settings", true), document, new ReplaceOptions().upsert(true));
    }

}
