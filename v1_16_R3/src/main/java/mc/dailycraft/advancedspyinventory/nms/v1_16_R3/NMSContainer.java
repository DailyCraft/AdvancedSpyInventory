package mc.dailycraft.advancedspyinventory.nms.v1_16_R3;

import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.IInventory;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.NonNullList;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class NMSContainer implements IInventory {
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
    public int getSize() {
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
                exception.printStackTrace();
            }
        }

        return CraftItemStack.asNMSCopy(item);
    }

    @Override
    public ItemStack splitStack(int index, int amount) {
        ItemStack stack = getItem(index);

        if (stack == ItemStack.b)
            return stack;
        else {
            ItemStack result;

            if (stack.getCount() <= amount) {
                setItem(index, ItemStack.b);
                result = stack;
            } else {
                result = CraftItemStack.copyNMSStack(stack, amount);
                stack.subtract(amount);
                setItem(index, stack);
            }

            return result;
        }
    }

    @Override
    public ItemStack splitWithoutUpdate(int index) {
        ItemStack stack = getItem(index);

        if (stack.isEmpty())
            return ItemStack.b;
        else {
            setItem(index, ItemStack.b);
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
    public void update() {
    }

    @Override
    public boolean a(EntityHuman player) {
        return true;
    }

    @Override
    public List<ItemStack> getContents() {
        return NonNullList.a(getSize(), ItemStack.b);
    }

    @Override
    public void onOpen(CraftHumanEntity entity) {
        viewers.add(entity);
        container.onOpen((Player) entity);
    }

    @Override
    public void onClose(CraftHumanEntity entity) {
        viewers.remove(entity);
        container.onClose((Player) entity);
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
    public void clear() {
    }
}