package dev.minechase.core.bukkit.menu.settings;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.settings.model.ISetting;
import dev.minechase.core.bukkit.util.WordUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SettingsMenu extends IPagedMenu {

    @Override
    public String getPageTitle(Player player) {
        return "Settings";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (ISetting setting : CorePlugin.getInstance().getSettingsHandler().getSortedSettings()) {
            buttons.add(new SettingButton(setting));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class SettingButton extends IButton {

        private final ISetting setting;

        @Override
        public ItemStack getItem(Player player) {
            boolean enabled = this.setting.isEnabled(player.getUniqueId());
            String toggleText = enabled ? "(( Click to disable this setting ))" :  "(( Click to enabled this setting ))";

            return new ItemFactory(Material.getMaterial(this.setting.getDisplayMaterial()))
                    .displayName(CC.blend(this.setting.getDisplayName(), this.setting.getPrimaryColor(), this.setting.getSecondaryColor(), "&l"))
                    .lore(WordUtil.wrapText(CC.blend(this.setting.getDescription(), "&7", "&f"), 30))
                    .addToLore(
                            " ",
                            "&fStatus&7: " + (enabled ? this.setting.getEnabledText() : this.setting.getDisabledText()),
                            " ",
                            CC.blend(toggleText, this.setting.getPrimaryColor(), this.setting.getSecondaryColor(), "&o")
                    )
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            boolean status = this.setting.toggle(player.getUniqueId());

            if (status) {
                player.sendMessage(CC.translate("<blend:&2;&a>[Settings] You enabled your " + this.setting.getDisplayName() + "</>"));
            } else {
                player.sendMessage(CC.translate("<blend:&4;&c>[Settings] You disabled your " + this.setting.getDisplayName() + "</>"));
            }

            updateMenu(player, true);
        }
    }

}
