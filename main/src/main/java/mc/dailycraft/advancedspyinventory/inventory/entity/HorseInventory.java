package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.utils.InformationItems;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class HorseInventory<T extends AbstractHorse> extends EntityInventory<T> {
    public HorseInventory(Player viewer, T entity, int rows) {
        super(viewer, entity, rows);
    }

    public HorseInventory(Player viewer, T entity) {
        this(viewer, entity, 3);
    }

    // Casting is mandatory for 1.12- support.
    @Override
    public ItemStack getItem(int index) {
        if (index == getSize() - 24)
            return getNonNull(((InventoryHolder) entity).getInventory().getItem(0), InformationItems.SADDLE.get(translation));
        else if (index == getSize() - 22)
            return getNonNull(((InventoryHolder) entity).getInventory().getItem(1), InformationItems.HORSE_ARMOR.get(translation));
        else if (index == getSize() - 16)
            return InformationItems.CHESTPLATE.unavailable(translation);

        return super.getItem(index);
    }

    // Casting is mandatory for 1.12- support.
    @Override
    public void setItem(int index, ItemStack stack) {
        if (index == getSize() - 24) {
            if (!stack.equals(InformationItems.SADDLE.get(translation)))
                ((InventoryHolder) entity).getInventory().setItem(0, stack);
        } else if (index == getSize() - 22) {
            if (!stack.equals(InformationItems.HORSE_ARMOR.get(translation)))
                ((InventoryHolder) entity).getInventory().setItem(1, stack);
        } else
            super.setItem(index, stack);
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot >= getSize() && Permissions.ENTITY_MODIFY.has(viewer)) {
            shift(event, getSize() - 24, InformationItems.SADDLE.get(translation), current -> current == Material.SADDLE);
            shift(event, getSize() - 22, InformationItems.HORSE_ARMOR.get(translation), current -> current.getKey().getKey().endsWith("_horse_armor"));
        }

        if (rawSlot == getSize() - 24) {
            if (Permissions.ENTITY_MODIFY.has(viewer))
                replaceItem(event, InformationItems.SADDLE.get(translation));
        } else if (rawSlot == getSize() - 22) {
            if (Permissions.ENTITY_MODIFY.has(viewer))
                replaceItem(event, InformationItems.HORSE_ARMOR.get(translation));
        } else if (rawSlot != getSize() - 16)
            super.onClick(event, rawSlot);
    }
}