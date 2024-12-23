package dev.minechase.core.bukkit.menu.grant;

import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ConversationBuilder;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GrantScopesMenu extends IPagedMenu {

    private final UUID targetUUID;
    private final UUID rankId;

    private final List<String> scopes = new ArrayList<>(Collections.singletonList("GLOBAL"));

    public GrantScopesMenu(UUID targetUUID, UUID rankId) {
        this.targetUUID = targetUUID;
        this.rankId = rankId;
    }

    @Override
    public String getPageTitle(Player player) {
        return "Select Scopes: " + UUIDUtils.getName(this.targetUUID);
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (String scope : CoreAPI.getScopes()) {
            buttons.add(new ScopeButton(scope));
        }

        return buttons;
    }

    @Override
    public Map<Integer, IButton> getGlobalButtons(Player player) {
        return new HashMap<>(){{
            put(5, new ConfirmButton());
        }};
    }

    @AllArgsConstructor
    public class ConfirmButton extends IButton {

        @Override
        public ItemStack getItem(Player player) {
            boolean valid = !scopes.isEmpty();
            String color = valid ? "&a&l" : "&c&l";

            return new ItemFactory(valid ? Material.GREEN_DYE : Material.RED_DYE)
                    .displayName(color + "Confirm Scopes")
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            boolean valid = !scopes.isEmpty();

            if (!valid) {
                player.sendMessage(CC.translate("<blend:&4;&c>You need to have at least one scope selected.</>"));
                return;
            }

            player.beginConversation(CoreConstants.getGrantDurationConversation(player, scopes, targetUUID, rankId));
            player.closeInventory();
        }
    }

    @AllArgsConstructor
    public class ScopeButton extends IButton {

        private final String scope;

        @Override
        public ItemStack getItem(Player player) {
            boolean active = scopes.contains(this.scope);
            String color = (active ? "&c" : "&a");

            return new ItemFactory(active ? Material.RED_BANNER : Material.GREEN_BANNER)
                    .displayName(color + "&l" + this.scope)
                    .lore(
                            "&7&m--------------------------",
                            color + "Click here to " + (active ? "removed" : "select") + " the " + this.scope + color + " scope.",
                            "&7&m--------------------------"
                    )
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            if (scopes.contains(this.scope)) {
                scopes.remove(this.scope);
            } else {
                scopes.add(this.scope);
            }

            updateMenu(player, true);
        }
    }

}
