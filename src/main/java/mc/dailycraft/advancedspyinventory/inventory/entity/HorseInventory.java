package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.utils.InformationItems;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import net.minecraft.server.v1_16_R3.ItemHorseArmor;
import net.minecraft.server.v1_16_R3.ItemStack;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftAbstractHorse;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HorseInventory<T extends AbstractHorse> extends EntityInventory<T> {
    public HorseInventory(Player viewer, T entity, int rows) {
        super(viewer, entity, rows);
    }

    public HorseInventory(Player viewer, T entity) {
        this(viewer, entity, 3);
    }

    @Override
    public List<ItemStack> getContents() {
        return Stream.concat(super.getContents().stream(), ((CraftAbstractHorse) entity).getHandle().inventoryChest.getContents().stream()).collect(Collectors.toList());
    }

    @Override
    public ItemStack getItem(int index) {
        if (index == getSize() - 24)
            return getNonNull(getContents().get(6), InformationItems.SADDLE.get(translation).nms());
        else if (index == getSize() - 22)
            return getNonNull(getContents().get(7), InformationItems.HORSE_ARMOR.get(translation).nms());
        else if (index == getSize() - 16)
            return InformationItems.CHESTPLATE.unavailable(translation);

        return super.getItem(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index == getSize() - 24) {
            if (!stack.equals(InformationItems.SADDLE.get(translation).nms())) {
                getContents().set(6, stack);
                ((CraftAbstractHorse) entity).getHandle().inventoryChest.setItem(0, stack);
            }
        } else if (index == getSize() - 22) {
            if (!stack.equals(InformationItems.HORSE_ARMOR.get(translation).nms())) {
                getContents().set(7, stack);
                ((CraftAbstractHorse) entity).getHandle().inventoryChest.setItem(1, stack);
            }
        } else
            super.setItem(index, stack);
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot >= getSize() && Permissions.ENTITY_MODIFY.has(viewer)) {
            shift(event, getSize() - 24, InformationItems.SADDLE.get(translation).get(), current -> CraftItemStack.asBukkitCopy(current).getType() == Material.SADDLE);
            shift(event, getSize() - 22, InformationItems.HORSE_ARMOR.get(translation).get(), current -> current.getItem() instanceof ItemHorseArmor);
        }

        if (rawSlot == getSize() - 24) {
            if (Permissions.ENTITY_MODIFY.has(viewer))
                replaceItem(event, InformationItems.SADDLE.get(translation).get());
        } else if (rawSlot == getSize() - 22) {
            if (Permissions.ENTITY_MODIFY.has(viewer))
                replaceItem(event, InformationItems.HORSE_ARMOR.get(translation).get());
        } else if (rawSlot != getSize() - 16)
            super.onClick(event, rawSlot);
    }
}