package dev.minechase.core.bukkit.api.event;

import dev.minechase.core.api.grant.grant.Grant;
import dev.minechase.core.api.punishment.model.Punishment;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

@Getter
@Setter
public class CoreChatEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private boolean cancelled, shadowMute = false;
    private final Player player;
    private final String message;
    private final List<Punishment> punishments;

    private Object packet;

    public CoreChatEvent(Player player, String message, List<Punishment> punishments) {
        super(true);

        this.player = player;
        this.message = message;
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
