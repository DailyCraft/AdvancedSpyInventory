package mc.dailycraft.advancedspyinventory.nms.v1_11_R1;

import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
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
    public String getName() {
        return container.getTitle();
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        return new ChatComponentText(getName());
    }

    @Override
    public int getSize() {
        return container.getSize();
    }

    @Override
    public boolean w_() {
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

        if (stack == ItemStack.a)
            return stack;
        else {
            ItemStack result;

            if (stack.getCount() <= amount) {
                setItem(index, ItemStack.a);
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
            return ItemStack.a;
        else {
            setItem(index, ItemStack.a);
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
    public void startOpen(EntityHuman human) {
    }

    @Override
    public void closeContainer(EntityHuman human) {
    }

    @Override
    public boolean b(int i, ItemStack itemStack) {
        return true;
    }

    @Override
    public int getProperty(int i) {
        return 0;
    }

    @Override
    public void setProperty(int i, int i1) {
    }

    @Override
    public int h() {
        return 0;
    }

    @Override
    public void clear() {
    }

    @Override
    public List<ItemStack> getContents() {
        return NonNullList.a(getSize(), ItemStack.a);
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
}