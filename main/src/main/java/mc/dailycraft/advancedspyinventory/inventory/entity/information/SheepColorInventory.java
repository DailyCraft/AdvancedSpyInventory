package mc.dailycraft.advancedspyinventory.inventory.entity.information;

import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SheepColorInventory extends InformationInventory<Sheep> {
    public SheepColorInventory(Player viewer, Sheep entity, CustomInventoryView oldView) {
        super(viewer, entity, oldView, 3);

        for (DyeColor color : DyeColor.values()) {
            contents[color.ordinal()] = new ItemStackBuilder(Material.getMaterial(color.name() + "_WOOL"), dyeToChatColor(color) + translation.format("interface.sheep.color." + color.name().toLowerCase()))
                    .lore(translation.format("interface.information.select" + (entity.getColor() == color ? "ed" : "")))
                    .enchant(entity.getColor() == color).get();
        }
    }

    @Override
    public String getTitle() {
        return translation.format("interface.sheep.title", entity.getName());
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot >= 0 && rawSlot <= 15) {
            if (entity.getColor() != DyeColor.values()[rawSlot]) {
                entity.setColor(DyeColor.values()[rawSlot]);
                oldView.open();
            }
        } else
            super.onClick(event, rawSlot);
    }

    public static String dyeToChatColor(DyeColor color) {
        StringBuilder sb = new StringBuilder("§x");

        for (char c : Integer.toHexString(color.getColor().asRGB()).toCharArray())
            sb.append('§').append(c);

        return sb.toString();
    }
}