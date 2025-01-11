package dev.minechase.core.velocity.lockdown;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.velocity.CoreLocale;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.util.CC;
import lombok.Getter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class LockdownHandler implements IModule {

    @Override
    public void load() {
        CoreVelocity.getInstance().getProxy().getEventManager().register(CoreVelocity.getInstance(), this);

        reload();
    }

    @Override
    public void unload() {

    }

    public UUID getMinimumRank() {
        Rank rank = CoreVelocity.getInstance().getRankHandler().getRank(CoreLocale.LOCK_DOWN_RANK.getString());
        if (rank == null) return null;
        return rank.getId();
    }

    public String getMessage() {
        return CoreLocale.LOCK_DOWN_MESSAGE.getString();
    }

    public long getDuration() {
        return CoreLocale.LOCK_DOWN_DURATION.getTimeDuration().transform();
    }

    public long getStartedAt() {
        return CoreLocale.LOCK_DOWN_STARTED_AT.getLong();
    }

    public boolean isActive() {
        return CoreLocale.LOCK_DOWN_ACTIVE.getBoolean();
    }

    public List<String> getBypassIps() {
        return CoreLocale.LOCK_DOWN_IP_BYPASS.getStringList();
    }

    public List<UUID> getBypassList() {
        return CoreLocale.LOCK_DOWN_BYPASS.getStringList().stream().map(UUID::fromString).collect(Collectors.toList());
    }

    public void activateLockDown(long duration) {
        CoreLocale.LOCK_DOWN_ACTIVE.update(true);
        CoreLocale.LOCK_DOWN_STARTED_AT.update(System.currentTimeMillis());
        CoreLocale.LOCK_DOWN_DURATION.update(duration);
        reload();
    }

    public void deactivateLockDown() {
        CoreLocale.LOCK_DOWN_ACTIVE.update(false);
        CoreLocale.LOCK_DOWN_DURATION.update(-1);
        CoreLocale.LOCK_DOWN_STARTED_AT.update(-1);
        reload();
    }

    @Subscribe(priority = Short.MAX_VALUE)
    public void onLogin(LoginEvent event) {
        User user = CoreVelocity.getInstance().getUserHandler().getUser(event.getPlayer().getUniqueId());

        if (CoreVelocity.getInstance().getLockdownHandler().isActive() && !getBypassIps().contains(event.getPlayer().getRemoteAddress().getAddress().getHostAddress())) {
            Rank rank = CoreVelocity.getInstance().getRankHandler().getRankById(CoreVelocity.getInstance().getLockdownHandler().getMinimumRank());

            if (rank != null) {
                Rank userRank = user.getActiveGrant().getRank();

                CoreVelocity.getInstance().getLogger().warning("[Arrow] " + userRank.getName() + " [" + userRank.getWeight() + "] ---> " + rank.getName() + " [" + rank.getWeight() + "]");

                if (rank.getWeight() < userRank.getWeight()) {
                    event.setResult(ResultedEvent.ComponentResult.denied(CC.translate(CoreVelocity.getInstance().getLockdownHandler().getMessage())));
                }
            } else {
                CoreVelocity.getInstance().getLogger().warning("[Arrow] It seems the lockdown handler is having problems processing the lockdown rank. No one will be able to join until this issue is resolved.");
                event.setResult(ResultedEvent.ComponentResult.denied(CC.translate(CoreVelocity.getInstance().getLockdownHandler().getMessage())));
            }
        }

    }

}
