package minecraft.dailycraft.advancedspyinventory.inventory;

import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftAbstractHorse;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;

import java.util.List;

public class HorseInventory extends EntityInventory
{
    private final AbstractHorse horse;

    public HorseInventory(Player sender, AbstractHorse horse)
    {
        super(sender, horse, 3);
        this.horse = horse;
    }

    @Override
    public List<ItemStack> getContents()
    {
        List<ItemStack> result = super.getContents();
        result.addAll(((CraftAbstractHorse) horse).getHandle().inventoryChest.getContents());
        return result;
    }

    @Override
    public ItemStack getItem(int index)
    {
        if (index == 3)
            return InventoryUtils.InformationItems.getItem(getContents().get(6), informationItems.saddle);
        else if (index == 5)
            return InventoryUtils.InformationItems.getItem(getContents().get(7), informationItems.horseArmor);
        else
            return super.getItem(index);
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        if (index == 3)
        {
            if (!stack.equals(informationItems.saddle))
            {
                getContents().set(6, stack);
                ((CraftAbstractHorse) horse).getHandle().inventoryChest.setItem(0, stack);
            }
        }
        else if (index == 5)
        {
            if (!stack.equals(informationItems.horseArmor))
            {
                getContents().set(7, stack);
                ((CraftAbstractHorse) horse).getHandle().inventoryChest.setItem(1, stack);
            }
        }
        else
            super.setItem(index, stack);
    }
}