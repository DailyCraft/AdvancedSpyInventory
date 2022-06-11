package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.utils.InformationItems;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class EndermanInventory extends EntityInventory<Enderman> {
    public EndermanInventory(Player viewer, Enderman entity) {
        super(viewer, entity, 3);
    }

    @Override
    public ItemStack[] getContents() {
        ItemStack[] result = new ItemStack[7];
        System.arraycopy(super.getContents(), 0, result, 0, 6);
        result[6] = new ItemStack(entity.getCarriedBlock().getMaterial());
        return result;
    }

    @Override
    public ItemStack getItem(int index) {
        return index == 4 ? getNonNull(getContents()[6], InformationItems.ENDERMAN_CARRIED.get(translation)) : super.getItem(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index == 4)
            entity.setCarriedBlock((getContents()[6] = stack).getType().createBlockData());
        else
            super.setItem(index, stack);
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot >= getSize() && Permissions.ENTITY_MODIFY.has(viewer))
            shift(event, 4, InformationItems.ENDERMAN_CARRIED.get(translation), current -> current.getType().isBlock());

        if (rawSlot == 4) {
            if (Permissions.ENTITY_MODIFY.has(viewer) && event.getCursor().getType().isBlock())
                replaceItem(event, InformationItems.ENDERMAN_CARRIED.get(translation));
        } else
            super.onClick(event, rawSlot);
    }
}