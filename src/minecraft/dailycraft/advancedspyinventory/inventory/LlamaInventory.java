package minecraft.dailycraft.advancedspyinventory.inventory;

import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLlama;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;

import java.util.List;

public class LlamaInventory extends EntityInventory
{
    private final Llama llama;

    public LlamaInventory(Player sender, Llama llama)
    {
        super(sender, llama, 6);
        this.llama = llama;
    }

    @Override
    public List<ItemStack> getContents()
    {
        List<ItemStack> result = super.getContents();
        result.addAll(((CraftLlama) llama).getHandle().inventoryChest.getContents());
        return result;
    }

    @Override
    public ItemStack getItem(int index)
    {
        if (llama.isCarryingChest())
        {
            if (llama.getStrength() == 1)
            {
                if (index >= 12 && index <= 14)
                    return getContents().get(index - 4);
            }
            else if (llama.getStrength() == 2)
            {
                if (index >= 3 && index <= 5)
                    return getContents().get(index + 5);
                else if (index >= 12 && index <= 14)
                    return getContents().get(index - 1);
            }
            else if (llama.getStrength() == 3)
            {
                if (index >= 9 && index <= 17)
                    return getContents().get(index - 1);
            }
            else if (llama.getStrength() == 4)
            {
                if (index <= 8)
                    return getContents().get(index + 8);
                else if (index >= 12 && index <= 14)
                    return getContents().get(index + 5);
            }
            else if (llama.getStrength() == 5)
            {
                if (index <= 8)
                    return getContents().get(index + 8);
                else if (index >= 10 && index <= 12)
                    return getContents().get(index + 7);
                else if (index >= 14 && index <= 16)
                    return getContents().get(index + 6);
            }
        }
        else
        {
            if (index == 13)
                return InventoryUtils.setItemWithDisplayName(Material.CHEST, translation.format("interface.donkey_and_mule.no_chest"));
        }

        if (index == 30)
            return InventoryUtils.InformationItems.getItem(getContents().get(6), InventoryUtils.InformationItems.addWarning(informationItems.saddle, translation));
        else if (index == 32)
            return InventoryUtils.InformationItems.getItem(getContents().get(7), informationItems.llamaDecor);
        else
            return super.getItem(index);
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        if (llama.isCarryingChest())
        {
            int i = 0;

            if (llama.getStrength() == 1)
            {
                if (index >= 12 && index <= 14)
                    i = -4;
            }
            else if (llama.getStrength() == 2)
            {
                if (index >= 3 && index <= 5)
                    i = 5;
                else if (index >= 12 && index <= 14)
                    i = -1;
            }
            else if (llama.getStrength() == 3)
            {
                if (index >= 9 && index <= 17)
                    i = -1;
            }
            else if (llama.getStrength() == 4)
            {
                if (index <= 8)
                    i = 8;
                else if (index >= 12 && index <= 14)
                    i = 5;
            }
            else if (llama.getStrength() == 5)
            {
                if (index <= 8)
                    i = 8;
                else if (index >= 10 && index <= 12)
                    i = 7;
                else if (index >= 14 && index <= 16)
                    i = 6;
            }

            if (i != 0)
            {
                getContents().set(index + i, stack);
                ((CraftLlama) llama).getHandle().inventoryChest.setItem(index + i - 6, stack);
            }
        }

        if (index == 30)
        {
            if (!stack.equals(InventoryUtils.InformationItems.addWarning(informationItems.saddle, translation)))
            {
                getContents().set(6, stack);
                ((CraftLlama) llama).getHandle().inventoryChest.setItem(0, stack);
            }
        }
        else if (index == 32)
        {
            if (!stack.equals(informationItems.llamaDecor))
            {
                getContents().set(7, stack);
                ((CraftLlama) llama).getHandle().inventoryChest.setItem(1, stack);
            }
        }
        else
            super.setItem(index, stack);
    }
}