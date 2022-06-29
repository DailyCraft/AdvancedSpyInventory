package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import org.bukkit.Material;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class DonkeyInventory extends HorseInventory<ChestedHorse> {
    public DonkeyInventory(Player viewer, ChestedHorse entity) {
        super(viewer, entity, 6);
    }

    @Override
    public ItemStack getItem(int index) {
        if (index > 1 && index <= 6 || index > 10 && index <= 15 || index > 19 && index <= 24) {
            if (entity.isCarryingChest())
                return entity.getInventory().getItem(index - (index <= 6 ? 0 : index <= 15 ? 4 : 8));
            else
                return new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE, translation.format("interface.donkey.no_chest")).get();
        }

        return super.getItem(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (entity.isCarryingChest() && (index > 1 && index <= 6 || index > 10 && index <= 15 || index > 19 && index <= 24))
            entity.getInventory().setItem(index - (index <= 6 ? 0 : index <= 15 ? 4 : 8), stack);

        super.setItem(index, stack);
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (entity.isCarryingChest() && Permissions.ENTITY_MODIFY.has(viewer) && (rawSlot > 1 && rawSlot <= 6 || rawSlot > 10 && rawSlot <= 15 || rawSlot > 19 && rawSlot <= 24))
            event.setCancelled(false);

        super.onClick(event, rawSlot);
    }
}