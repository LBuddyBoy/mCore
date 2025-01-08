package dev.minechase.core.bukkit.api.event;

import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.model.PunishmentSnapshot;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AsyncCoreLoginEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private boolean cancelled;
    private final UUID uniqueId;
    private final String name;
    private final List<PunishmentSnapshot> punishments;
    private String kickMessage;

    public AsyncCoreLoginEvent(UUID uniqueId, String name, List<PunishmentSnapshot> punishments) {
        super(true);

        this.uniqueId = uniqueId;
        this.name = name;
        this.punishments = punishments;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
