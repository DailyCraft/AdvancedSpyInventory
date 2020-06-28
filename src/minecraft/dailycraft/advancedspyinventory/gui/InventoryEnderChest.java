package minecraft.dailycraft.advancedspyinventory.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

import static minecraft.dailycraft.advancedspyinventory.gui.InventoryUtils.*;

public class InventoryEnderChest extends InventoryBase
{
    public InventoryEnderChest(Player sender, UUID uuid, JavaPlugin plugin)
    {
        super(sender, uuid, plugin, "enderchest");
    }

    @Override
    public int getSize()
    {
        return 9 * 4;
    }

    @Override
    public String getName()
    {
        return translation.translate("" + ChatColor.DARK_RED + ChatColor.BOLD + Bukkit.getOfflinePlayer(uuid).getName() + ChatColor.RESET + ChatColor.YELLOW + "'s Ender Chest", ChatColor.YELLOW + "Coffre de l'Ender de " + ChatColor.DARK_RED + ChatColor.BOLD + Bukkit.getOfflinePlayer(uuid).getName());
    }

    @Override
    public ItemStack getItem(int index)
    {
        if (index <= getSize() - 10)
        {
            return super.getItem(index);
        }
        else if (index == getSize() - 5)
        {
            return setItemWithDisplayName(Material.BARRIER, translation.translate("Clear Ender Chest", "Vider le Coffre de l'Ender"));
        }

        return getVoidItem();
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        if (index <= getSize() - 10)
        {
            super.setItem(index, stack);
        }
    }
}