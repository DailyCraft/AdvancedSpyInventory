package mc.dailycraft.advancedspyinventory.nms.v1_16_R3;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public class NMSContainer implements IInventory {
    private final mc.dailycraft.advancedspyinventory.nms.NMSContainer container;
    private final List<HumanEntity> viewers = new ArrayList<>();

    public NMSContainer(mc.dailycraft.advancedspyinventory.nms.NMSContainer container) {
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
        return CraftItemStack.asNMSCopy(container.getItem(index));
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
    public boolean a(EntityHuman human) {
        return true;
    }

    @Override
    public List<ItemStack> getContents() {
        org.bukkit.inventory.ItemStack[] contents = container.getContents();
        List<ItemStack> result = NonNullList.a(contents.length, ItemStack.b);

        for (int i = 0; i < contents.length; ++i)
            if (contents[i] != null && contents[i].getType() != Material.AIR)
                result.set(i, CraftItemStack.asNMSCopy(contents[i]));

        return result;
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
    public void clear() {
    }
}