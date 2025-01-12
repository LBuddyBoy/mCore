package dev.minechase.core.api.note;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.note.cache.NoteCacheLoader;
import dev.minechase.core.api.note.comparator.NoteComparator;
import dev.minechase.core.api.note.model.Note;
import dev.minechase.core.api.note.packet.NoteUpdatePacket;
import dev.minechase.core.api.note.cache.NoteCacheLoader;
import dev.minechase.core.api.note.model.Note;
import dev.minechase.core.api.user.model.User;
import lombok.Getter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Getter
public class NoteHandler implements IModule {

    private MongoCollection<Document> collection;
    private AsyncLoadingCache<UUID, List<Note>> notes;

    @Override
    public void load() {
        this.notes = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .buildAsync(new NoteCacheLoader());

        this.collection = CoreAPI.getInstance().getMongoHandler().getDatabase().getCollection("Notes");
    }

    @Override
    public void unload() {

    }

    public CompletableFuture<List<Note>> fetchAllNotes() {
        return CompletableFuture.supplyAsync(() -> this.collection.find().map(Note::new).into(new ArrayList<>()), CoreAPI.POOL);
    }

    public void updateNote(Note note) {
        CompletableFuture<List<Note>> notesIfPresent = this.notes.getIfPresent(note.getTargetUUID());

        if (notesIfPresent != null) {
            notesIfPresent.whenCompleteAsync((notes, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                }

                notes.removeIf(p -> p.getId().equals(note.getId()));
                notes.add(note);

                this.notes.put(note.getTargetUUID(), CompletableFuture.completedFuture(notes));
            });
        }
    }

    public List<Note> updateNoteExpiry(List<Note> notes) {
        for (Note note : notes) {
            if (note.isRemoved()) continue;
            if (!note.isExpired()) continue;

            note.remove(null, "Expired");
            new NoteUpdatePacket(note).send();
        }

        return notes;
    }

    public void saveNote(Note note, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> saveNote(note, false), CoreAPI.POOL);
            return;
        }

        this.collection.replaceOne(Filters.eq("id", note.getId().toString()), note.toDocument(), new ReplaceOptions().upsert(true));
    }

    public CompletableFuture<List<Note>> getNotes(UUID targetUUID) {
        CompletableFuture<List<Note>> notesIfPresent = this.notes.getIfPresent(targetUUID);

        if (notesIfPresent != null) return notesIfPresent.thenApplyAsync(this::updateNoteExpiry);

        return this.notes.get(targetUUID).thenApplyAsync(this::updateNoteExpiry);
    }

    public CompletableFuture<List<Note>> getSortedNotes(UUID targetUUID) {
        return this.getNotes(targetUUID).thenApplyAsync(notes -> notes.stream().sorted(new NoteComparator()).toList());
    }

    public CompletableFuture<List<Note>> getValidNotes(UUID targetUUID) {
        return this.getNotes(targetUUID).thenApplyAsync(notes -> notes.stream().filter(
                note -> note.isValidLocal() && !note.isRemoved() && !note.isExpired()
        ).toList());
    }

}
