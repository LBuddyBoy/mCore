package dev.minechase.core.bukkit.api;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.api.report.ReportHandler;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitReportHandler extends ReportHandler {

    @Override
    public void load() {
        super.load();

        Tasks.runAsyncTimer(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());
                if (user.getPendingMessages().isEmpty()) continue;

                for (String message : user.getPendingMessages()) {
                    player.sendMessage(CC.translate(message));
                }

                user.getPendingMessages().clear();
            }
        }, 20, 20);
    }
}
