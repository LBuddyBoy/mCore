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
public class PlayerInteractHologramEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private boolean cancelled;
    private final Player player;
    private final IHologram hologram;
    private final boolean leftClick;

    public PlayerInteractHologramEvent(Player player, IHologram hologram, boolean leftClick) {
        super(true);
        this.player = player;
        this.hologram = hologram;
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
