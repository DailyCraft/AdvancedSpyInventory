package minecraft.dailycraft.advancedspyinventory.inventory;

import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEnderman;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.List;

public class EndermanInventory extends EntityInventory
{
    private final Enderman enderman;

    public EndermanInventory(Player sender, Enderman enderman)
    {
        super(sender, enderman, 3);

        this.enderman = enderman;
    }

    @Override
    public List<ItemStack> getContents()
    {
        List<ItemStack> result = super.getContents();

        try
        {
            result.add(new ItemStack(((CraftEnderman) enderman).getHandle().getCarried().getBlock()));
        }
        catch (NullPointerException exception)
        {
            result.add(ItemStack.a);
        }

        return result;
    }

    @Override
    public ItemStack getItem(int index)
    {
        if (index == 4)
            return getContents().get(6);
        else
            return super.getItem(index);
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        if (index == 4)
        {
            getContents().set(6, stack);
            enderman.setCarriedMaterial(new MaterialData(CraftItemStack.asBukkitCopy(stack).getType()));
        }
        else
            super.setItem(index, stack);
    }
}