package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import org.bukkit.DyeColor;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class DonkeyInventory extends HorseInventory<ChestedHorse> {
    public DonkeyInventory(Player viewer, ChestedHorse entity) {
        super(viewer, entity, 6);
    }

    // Casting is mandatory for 1.12- support.
    @Override
    public ItemStack getItem(int index) {
        if (index > 1 && index <= 6 || index > 10 && index <= 15 || index > 19 && index <= 24) {
            if (entity.isCarryingChest())
                return ((InventoryHolder) entity).getInventory().getItem(index - (index <= 6 ? 0 : index <= 15 ? 4 : 8));
            else
                return ItemStackBuilder.ofStainedGlassPane(DyeColor.BLACK, translation.format("interface.donkey.no_chest")).get();
        }

        return super.getItem(index);
    }

    // Casting is mandatory for 1.12- support.
    @Override
    public void setItem(int index, ItemStack stack) {
        if (entity.isCarryingChest() && (index > 1 && index <= 6 || index > 10 && index <= 15 || index > 19 && index <= 24))
            ((InventoryHolder) entity).getInventory().setItem(index - (index <= 6 ? 0 : index <= 15 ? 4 : 8), stack);

        super.setItem(index, stack);
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (entity.isCarryingChest() && Permissions.ENTITY_MODIFY.has(viewer) && (rawSlot > 1 && rawSlot <= 6 || rawSlot > 10 && rawSlot <= 15 || rawSlot > 19 && rawSlot <= 24))
            event.setCancelled(false);

        super.onClick(event, rawSlot);
    }
}