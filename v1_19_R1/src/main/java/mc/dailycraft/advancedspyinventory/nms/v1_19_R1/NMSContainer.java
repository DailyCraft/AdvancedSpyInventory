package mc.dailycraft.advancedspyinventory.nms.v1_19_R1;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public class NMSContainer implements Container {
    private final mc.dailycraft.advancedspyinventory.nms.NMSContainer container;
    private final List<HumanEntity> viewers = new ArrayList<>();

    public NMSContainer(mc.dailycraft.advancedspyinventory.nms.NMSContainer container) {
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
        ItemStack stack = CraftItemStack.asNMSCopy(container.getItem(index));
        CompoundTag tag = stack.getTagElement("SkullOwner");

        if (tag != null) {
            String name = tag.getString("Name");
            stack.removeTagKey("SkullOwner");
            stack.getTag().putString("SkullOwner", name);
        }

        return stack;
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
        org.bukkit.inventory.ItemStack[] contents = container.getContents();
        List<ItemStack> result = NonNullList.withSize(contents.length, ItemStack.EMPTY);

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
    public void clearContent() {
    }
}