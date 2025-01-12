package dev.minechase.core.api.note.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.note.model.Note;
import dev.minechase.core.api.packet.ServerResponsePacket;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NoteUpdatePacket extends ServerResponsePacket {

    private final Note note;
    private final String executeServer;

    public NoteUpdatePacket(Note note) {
        this.note = note;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getNoteHandler().updateNote(this.note);
        CoreAPI.getInstance().getNoteHandler().saveNote(this.note, true);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getNoteHandler().updateNote(this.note);
    }

}
