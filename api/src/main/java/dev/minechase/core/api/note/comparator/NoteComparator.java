package dev.minechase.core.api.note.comparator;

import dev.minechase.core.api.grant.model.Grant;
import dev.minechase.core.api.note.model.Note;

import java.util.Comparator;

public class NoteComparator implements Comparator<Note> {

    @Override
    public int compare(Note g1, Note g2) {
        int weight = 0;

        weight += Long.compare(g2.getTimeLeft(), g1.getTimeLeft());
        weight += Boolean.compare(g1.isRemoved(), g2.isRemoved());

        return weight;
    }

}
