package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.utils.InformationItems;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import net.minecraft.server.v1_16_R3.ItemStack;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEnderman;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class EndermanInventory extends EntityInventory<Enderman> {
    public EndermanInventory(Player viewer, Enderman entity) {
        super(viewer, entity, 3);
    }

    @Override
    public List<ItemStack> getContents() {
        List<ItemStack> result = super.getContents();

        try {
            result.add(new ItemStack(((CraftEnderman) entity).getHandle().getCarried().getBlock()));
        } catch (NullPointerException exception) {
            result.add(ItemStack.b);
        }

        return result;
    }

    @Override
    public ItemStack getItem(int index) {
        return index == 4 ? getNonNull(getContents().get(6), InformationItems.ENDERMAN_CARRIED.get(translation).nms()) : super.getItem(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index == 4) {
            getContents().set(6, stack);
            entity.setCarriedBlock(CraftItemStack.asBukkitCopy(stack).getType().createBlockData());
        } else
            super.setItem(index, stack);
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot >= getSize() && Permissions.ENTITY_MODIFY.has(viewer))
            shift(event, 4, InformationItems.ENDERMAN_CARRIED.get(translation).get(), current -> CraftItemStack.asBukkitCopy(current).getType().isBlock());

        if (rawSlot == 4) {
            if (Permissions.ENTITY_MODIFY.has(viewer) && event.getCursor().getType().isBlock())
                replaceItem(event, InformationItems.ENDERMAN_CARRIED.get(translation).get());
        } else
            super.onClick(event, rawSlot);
    }
}