package mc.dailycraft.advancedspyinventory.inventory.entity.information;

import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class SheepColorInventory extends InformationInventory<Sheep> {
    public SheepColorInventory(Player viewer, Sheep entity, InventoryView oldView) {
        super(viewer, entity, oldView, 3);

        for (DyeColor color : DyeColor.values())
            contents[color.ordinal()] = new ItemStackBuilder(new ItemStack(Material.getMaterial(color.name() + "_WOOL")), translation.formatColor(color))
                    .lore(translation.format("interface.information.select" + (entity.getColor() == color ? "ed" : "")))
                    .enchant(entity.getColor() == color).get();
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
                openOld();
            }
        } else
            super.onClick(event, rawSlot);
    }
}