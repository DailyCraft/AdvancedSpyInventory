package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.utils.InformationItems;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class EndermanInventory extends EntityInventory<Enderman> {
    public EndermanInventory(Player viewer, Enderman entity) {
        super(viewer, entity, 3);
    }

    @Override
    public ItemStack getItem(int index) {
        if (index == 4) {
            if (Main.VERSION >= 13) {
                BlockData carried = entity.getCarriedBlock();
                return getNonNull(carried != null ? new ItemStack(carried.getMaterial()) : null, InformationItems.ENDERMAN_CARRIED.get(translation));
            } else {
                MaterialData carried = entity.getCarriedMaterial();
                return getNonNull(new ItemStack(carried.getItemType(), 1, carried.getData()), InformationItems.ENDERMAN_CARRIED.get(translation));
            }
        } else
            return super.getItem(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index == 4) {
            if (Main.VERSION >= 13)
                entity.setCarriedBlock(stack.getType().createBlockData());
            else
                entity.setCarriedMaterial(stack.getData());
        } else
            super.setItem(index, stack);
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot >= getSize() && Permissions.ENTITY_MODIFY.has(viewer))
            shift(event, 4, InformationItems.ENDERMAN_CARRIED.get(translation), Material::isBlock);

        if (rawSlot == 4) {
            if (Permissions.ENTITY_MODIFY.has(viewer) && event.getCursor().getType().isBlock())
                replaceItem(event, InformationItems.ENDERMAN_CARRIED.get(translation));
        } else
            super.onClick(event, rawSlot);
    }
}