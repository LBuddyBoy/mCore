package dev.minechase.core.api.report;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.grant.cache.GrantCacheLoader;
import dev.minechase.core.api.grant.model.Grant;
import dev.minechase.core.api.report.model.Report;
import dev.minechase.core.api.report.model.Report;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Getter
public class ReportHandler implements IModule {
    
    private MongoCollection<Document> collection;
    private Map<UUID, Report> reports;
    
    @Override
    public void load() {
        this.reports = new ConcurrentHashMap<>();
        this.collection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("Reports");

        for (Document document : this.collection.find()) {
            Report report = new Report(document);

            this.reports.put(report.getId(), report);
        }
    }

    @Override
    public void unload() {

    }

    public void updateReport(Report report) {
        this.reports.put(report.getId(), report);
    }

    public void removeReport(Report report) {
        this.reports.remove(report.getId());
    }

    public void deleteReport(Report report, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> deleteReport(report, false), CoreAPI.POOL);
            return;
        }

        this.collection.deleteOne(Filters.eq("id", report.getId().toString()));
    }

    public void saveReport(Report report, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> saveReport(report, false), CoreAPI.POOL);
            return;
        }

        this.collection.replaceOne(Filters.eq("id", report.getId().toString()), report.toDocument(), new ReplaceOptions().upsert(true));
    }

}
