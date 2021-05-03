package minecraft.dailycraft.advancedspyinventory.inventory;

import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.material.SpawnEgg;

import java.util.List;

public class VillagerInventory extends EntityInventory
{
    private final Villager villager;

    public VillagerInventory(Player sender, Villager villager)
    {
        super(sender, villager, 4);
        this.villager = villager;
    }


    @Override
    public List<ItemStack> getContents()
    {
        List<ItemStack> result = super.getContents();
        result.addAll(((CraftVillager) villager).getHandle().inventory.getContents());

        return result;
    }

    @Override
    public ItemStack getItem(int index)
    {
        if (index >= 2 && index <= 6)
            return getContents().get(index + 4);
        else if (index >= 12 && index <= 14)
            return getContents().get(index - 1);
        else if (index == getSize() - 3)
            return InventoryUtils.setItemWithDisplayName(new SpawnEgg(EntityType.VILLAGER).toItemStack(1), translation.format("interface.villager.profession", villager.getProfession()));
        else
            return super.getItem(index);
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        if (index >= 2 && index <= 6)
        {
            getContents().set(index + 4, stack);
            ((CraftVillager) villager).getHandle().inventory.setItem(index - 2, stack);
        }
        else if (index >= 12 && index <= 14)
        {
            getContents().set(index - 1, stack);
            ((CraftVillager) villager).getHandle().inventory.setItem(index - 7, stack);
        }
        else
            super.setItem(index, stack);
    }
}