package minecraft.dailycraft.advancedspyinventory.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class InventoryUtils
{
    public static ItemStack setItemWithDisplayName(ItemStack stack, String displayName, String... lore)
    {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(Arrays.asList(lore));
        stack.setItemMeta(meta);

        return stack;
    }

    public static ItemStack setItemWithDisplayName(Material material, String displayName, String... lore)
    {
        return setItemWithDisplayName(new ItemStack(material), displayName, lore);
    }

    public static ItemStack getVoidItem()
    {
        return setItemWithDisplayName(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), " ");
    }
}
