package minecraft.dailycraft.advancedspyinventory.inventory;

import minecraft.dailycraft.advancedspyinventory.utils.TranslationUtils;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInventory implements IInventory
{
    private final List<HumanEntity> viewers = new ArrayList<>();
    private final int inventorySize;

    protected final TranslationUtils translation;
    protected final InventoryUtils.InformationItems informationItems;

    public AbstractInventory(TranslationUtils translation, int inventoryColumns)
    {
        inventorySize = inventoryColumns * 9;

        this.translation = translation;
        informationItems = new InventoryUtils.InformationItems(translation);
    }

    @Override
    public int getSize()
    {
        return inventorySize;
    }

    @Override
    public boolean x_()
    {
        return false;
    }

    @Override
    public ItemStack splitStack(int index, int amount)
    {
        ItemStack stack = getItem(index);

        if (stack == ItemStack.a)
        {
            return stack;
        }
        else
        {
            ItemStack result;

            if (stack.getCount() <= amount)
            {
                setItem(index, ItemStack.a);
                result = stack;
            }
            else
            {
                result = CraftItemStack.copyNMSStack(stack, amount);
                stack.subtract(amount);
                setItem(index, stack);
            }

            return result;
        }
    }

    @Override
    public ItemStack splitWithoutUpdate(int index)
    {
        ItemStack stack = getItem(index);

        if (stack.isEmpty())
        {
            return ItemStack.a;
        }
        else
        {
            setItem(index, ItemStack.a);
            return stack;
        }
    }

    @Override
    public int getMaxStackSize()
    {
        return 64;
    }

    @Override
    public void update()
    {}

    @Override
    public boolean a(EntityHuman human)
    {
        return true;
    }

    @Override
    public void startOpen(EntityHuman human)
    {}

    @Override
    public void closeContainer(EntityHuman human)
    {}

    @Override
    public boolean b(int index, ItemStack stack)
    {
        return true;
    }

    @Override
    public int getProperty(int i)
    {
        return 0;
    }

    @Override
    public void setProperty(int i, int i1)
    {}

    @Override
    public int h()
    {
        return 0;
    }

    @Override
    public void clear()
    {}

    @Override
    public void onOpen(CraftHumanEntity craftHuman)
    {
        viewers.add(craftHuman);
    }

    @Override
    public void onClose(CraftHumanEntity craftHuman)
    {
        viewers.remove(craftHuman);
    }

    @Override
    public List<HumanEntity> getViewers()
    {
        return viewers;
    }

    @Override
    public InventoryHolder getOwner()
    {
        return null;
    }

    @Override
    public void setMaxStackSize(int size)
    {}

    @Override
    public Location getLocation()
    {
        return null;
    }

    @Override
    public boolean hasCustomName()
    {
        return getName() != null;
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName()
    {
        return new ChatComponentText(getName());
    }
}