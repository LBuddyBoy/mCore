package dev.minechase.core.bukkit.menu.punishments;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.util.HeadUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PunishmentsMainMenu extends IMenu {

    private final UUID targetUUID;
    private final List<Punishment> punishments;

    public PunishmentsMainMenu(UUID targetUUID, List<Punishment> punishments) {
        this.targetUUID = targetUUID;
        this.punishments = punishments;
    }

    @Override
    public String getTitle(Player player) {
        return UUIDUtils.getName(this.targetUUID) + "'s Punishments";
    }

    @Override
    public int getSize(Player player) {
        return 9;
    }

    @Override
    public boolean autoFills(Player player) {
        return true;
    }

    @Override
    public Map<Integer, IButton> getButtons(Player player) {
        Map<Integer, IButton> buttons = new HashMap<>();

        buttons.put(2, new PunishmentTypeButton(PunishmentType.WARN, HeadUtil.GRAY_BASE_64));
        buttons.put(3, new PunishmentTypeButton(PunishmentType.KICK, HeadUtil.DARK_GREEN_BASE_64));
        buttons.put(5, new PunishmentTypeButton(PunishmentType.MUTE, HeadUtil.DARK_RED_BASE_64));
        buttons.put(7, new PunishmentTypeButton(PunishmentType.BAN, HeadUtil.YELLOW_BASE_64));
        buttons.put(8, new PunishmentTypeButton(PunishmentType.BLACKLIST, HeadUtil.BLACK_BASE_64));

        return buttons;
    }

    @AllArgsConstructor
    public class PunishmentTypeButton extends IButton {

        private final PunishmentType type;
        private final String headTexture;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(headTexture)
                    .displayName(CC.blend(this.type.getDisplayName() + "s", this.type.getPrimaryColor(), this.type.getSecondaryColor(), "&l"))
                    .lore(Arrays.asList(
                            "&f" + this.type.getDisplayName() + "s&7: " + this.type.getSecondaryColor() + punishments.stream().filter(punishment -> punishment.getType() == this.type).count(),
                            " ",
                            CC.blend("(( Click to view all " + this.type.getDisplayName().toLowerCase() + "s ))", this.type.getPrimaryColor(), this.type.getSecondaryColor(), "&o")
                    ))
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            new ViewPunishmentsMenu(targetUUID, punishments, this.type).openMenu(player);
        }
    }

}
