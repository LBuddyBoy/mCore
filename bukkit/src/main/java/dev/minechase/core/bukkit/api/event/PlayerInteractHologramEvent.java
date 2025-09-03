package dev.minechase.core.bukkit.api.event;

import dev.minechase.core.bukkit.hologram.model.HologramLine;
import dev.minechase.core.bukkit.hologram.model.IHologram;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class PlayerInteractHologramLineEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private boolean cancelled;
    private final Player player;
    private final IHologram hologram;
    private final HologramLine line;
    private final boolean leftClick;

    public PlayerInteractHologramLineEvent(Player player, IHologram hologram, HologramLine line, boolean leftClick) {
        super(true);
        this.player = player;
        this.hologram = hologram;
        this.line = line;
        this.leftClick = leftClick;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
