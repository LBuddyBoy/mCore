package dev.minechase.core.bukkit.api.event;

import dev.minechase.core.api.punishment.model.PunishmentSnapshot;
import dev.minechase.core.bukkit.hologram.model.HologramLine;
import dev.minechase.core.bukkit.hologram.model.IHologram;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

@Getter
@Setter
public class PlayerInteractHologramEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private boolean cancelled;
    private final Player player;
    private final IHologram hologram;
    private final HologramLine line;
    private final boolean leftClick;

    public PlayerInteractHologramEvent(Player player, IHologram hologram, HologramLine line, boolean leftClick) {
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
