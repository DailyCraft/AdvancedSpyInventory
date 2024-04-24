package mc.dailycraft.advancedspyinventory.nms.v1_20_R4;

import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class NMSContainer implements Container {
    private static final Field handle;

    static {
        try {
            (handle = CraftItemStack.class.getDeclaredField("handle")).setAccessible(true);
        } catch (NoSuchFieldException exception) {
            throw new RuntimeException(exception);
        }
    }

    private final BaseInventory container;
    private final List<HumanEntity> viewers = new ArrayList<>();

    public NMSContainer(BaseInventory container) {
        this.container = container;
    }

    @Override
    public int getContainerSize() {
        return container.getSize();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int index) {
        org.bukkit.inventory.ItemStack item = container.getItem(index);

        if (item instanceof CraftItemStack) {
            try {
                return (ItemStack) handle.get(item);
            } catch (IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }

        return CraftItemStack.asNMSCopy(item);
    }

    @Override
    public ItemStack removeItem(int index, int amount) {
        ItemStack stack = getItem(index);

        if (stack == ItemStack.EMPTY)
            return stack;
        else {
            ItemStack result;

            if (stack.getCount() <= amount) {
                setItem(index, ItemStack.EMPTY);
                result = stack;
            } else {
                result = CraftItemStack.copyNMSStack(stack, amount);
                stack.shrink(amount);
                setItem(index, stack);
            }

            return result;
        }
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = getItem(index);

        if (stack.isEmpty())
            return ItemStack.EMPTY;
        else {
            setItem(index, ItemStack.EMPTY);
            return stack;
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        container.setItem(index, CraftItemStack.asBukkitCopy(stack));
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public List<ItemStack> getContents() {
        return NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public void onOpen(CraftHumanEntity entity) {
        viewers.add(entity);
        container.onOpen((org.bukkit.entity.Player) entity);
    }

    @Override
    public void onClose(CraftHumanEntity entity) {
        viewers.remove(entity);
        container.onClose((org.bukkit.entity.Player) entity);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return viewers;
    }

    @Override
    public InventoryHolder getOwner() {
        return null;
    }

    @Override
    public void setMaxStackSize(int size) {
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void clearContent() {
    }
}