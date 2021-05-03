package minecraft.dailycraft.advancedspyinventory.inventory;

import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftChestedHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Player;

import java.util.List;

public class DonkeyAndMuleInventory extends EntityInventory
{
    private final ChestedHorse chestedHorse;

    public DonkeyAndMuleInventory(Player sender, ChestedHorse chestedHorse)
    {
        super(sender, chestedHorse, 6);

        this.chestedHorse = chestedHorse;
    }

    @Override
    public List<ItemStack> getContents()
    {
        List<ItemStack> result = super.getContents();
        result.addAll(((CraftChestedHorse) chestedHorse).getHandle().inventoryChest.getContents());
        return result;
    }

    @Override
    public ItemStack getItem(int index)
    {
        if (chestedHorse.isCarryingChest())
        {
            if (index <= 8)
                return getContents().get(index + 8);
            else if (index >= 10 && index <= 12)
                return getContents().get(index + 7);
            else if (index >= 14 && index <= 16)
                return getContents().get(index + 6);
        }
        else
        {
            if (index == 13)
                return InventoryUtils.setItemWithDisplayName(Material.CHEST, translation.format("interface.donkey_and_mule.no_chest"));
        }

        if (index == 30)
            return InventoryUtils.InformationItems.getItem(getContents().get(6), informationItems.saddle);
        else if (index == 32)
            return InventoryUtils.InformationItems.getItem(getContents().get(7), InventoryUtils.InformationItems.addWarning(informationItems.horseArmor, translation));
        else
            return super.getItem(index);
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        if (chestedHorse.isCarryingChest())
        {
            if (index <= 8)
            {
                getContents().set(index + 8, stack);
                ((CraftChestedHorse) chestedHorse).getHandle().inventoryChest.setItem(index + 2, stack);
            }
            else if (index >= 10 && index <= 12)
            {
                getContents().set(index + 7, stack);
                ((CraftChestedHorse) chestedHorse).getHandle().inventoryChest.setItem(index + 1, stack);
            }
            else if (index >= 14 && index <= 16)
            {
                getContents().set(index + 6, stack);
                ((CraftChestedHorse) chestedHorse).getHandle().inventoryChest.setItem(index, stack);
            }
        }

        if (index == 30)
        {
            if (!stack.equals(informationItems.saddle))
            {
                getContents().set(6, stack);
                ((CraftChestedHorse) chestedHorse).getHandle().inventoryChest.setItem(0, stack);
            }
        }
        else if (index == 32)
        {
            if (!stack.equals(InventoryUtils.InformationItems.addWarning(informationItems.horseArmor, translation)))
            {
                getContents().set(7, stack);
                ((CraftChestedHorse) chestedHorse).getHandle().inventoryChest.setItem(1, stack);
            }
        }
        else
            super.setItem(index, stack);
    }
}