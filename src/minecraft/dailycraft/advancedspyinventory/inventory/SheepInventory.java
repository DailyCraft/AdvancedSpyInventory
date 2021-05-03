package minecraft.dailycraft.advancedspyinventory.inventory;

import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;

public class SheepInventory extends EntityInventory
{
    private final Sheep sheep;

    public SheepInventory(Player sender, Sheep sheep)
    {
        super(sender, sheep);
        this.sheep = sheep;
    }

    @Override
    public ItemStack getItem(int index)
    {
        if (index == getSize() - 3)
            return InventoryUtils.setItemWithDisplayName(new org.bukkit.inventory.ItemStack(Material.WOOL, 1, sheep.getColor().getWoolData()), translation.format("interface.sheep.color", sheep.getColor().name()));
        else
            return super.getItem(index);
    }
}