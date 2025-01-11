package dev.minechase.core.api.tag.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.grant.model.Grant;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.tag.model.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TagUpdatePacket extends ServerResponsePacket {

    private final Tag tag;
    private final String executeServer;

    public TagUpdatePacket(Tag tag) {
        this.tag = tag;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getTagHandler().updateTag(this.tag);
        CoreAPI.getInstance().getTagHandler().saveTag(this.tag);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getTagHandler().updateTag(this.tag);
    }

}
