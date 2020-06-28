package minecraft.dailycraft.advancedspyinventory.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.text.DecimalFormat;
import java.util.*;

import static minecraft.dailycraft.advancedspyinventory.gui.InventoryUtils.*;

public class InventoryPlayer extends InventoryBase
{
    public InventoryPlayer(Player sender, UUID uuid, JavaPlugin plugin)
    {
        super(sender, uuid, plugin, "inventory");
    }

    @Override
    public String getName()
    {
        return translation.translate("§4§l" + Bukkit.getOfflinePlayer(uuid).getName() + "§r§e's Inventory", "§eInventaire de §4§l" + Bukkit.getOfflinePlayer(uuid).getName());
    }

    @Override
    public int getSize()
    {
        return 9 * 6;
    }

    @Override
    public ItemStack getItem(int index)
    {
        if (index <= 26)
        {
            return super.getItem(index + 9);
        }

        if (index <= 35)
        {
            return super.getItem(index - 27);
        }

        switch (index)
        {
            case 36:
                return super.getItem(9 * 4 + 3);

            case 37:
                return super.getItem(9 * 4 + 2);

            case 38:
                return super.getItem(9 * 4 + 1);

            case 39:
                return super.getItem(9 * 4);

            case 44:
                return super.getItem(9 * 4 + 4);

            case 46:
                return setItemWithDisplayName(new Potion(PotionType.INSTANT_HEAL).toItemStack(1), translation.translate("Health", "Vie") + " : " + new DecimalFormat(".#").format(getHealth()).replace(',', '.'));

            case 47:
                if (translation.getSender().hasPermission("advancedspyinventory.inventory.location.view"))
                {
                    return setItemWithDisplayName(Material.ARROW, "Location",
                            translation.translate("World", "Monde") + " : " + getWorldName(),
                            "X : " + new DecimalFormat(".##").format(getLocation("x")).replace(',', '.'),
                            "Y : " + new DecimalFormat(".##").format(getLocation("y")).replace(',', '.'),
                            "Z : " + new DecimalFormat(".##").format(getLocation("z")).replace(',', '.'), "",
                            "-> " + translation.translate("Click to teleport", "Cliquer pour se téléporter"));
                }

                break;

            case 49:
                return setItemWithDisplayName(Material.BARRIER, translation.translate("Clear Inventory", "Vider l'inventaire"));

            case 51:
                return setItemWithDisplayName(Material.EXP_BOTTLE, "Experience : " + getExperience() + " points");

            case 52:
                return setItemWithDisplayName(Material.COOKED_BEEF, translation.translate("Food", "Nourriture") + " : " + getFood());
        }

        return getVoidItem();
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        if (index <= 26)
        {
            super.setItem(index + 9, stack);
            return;
        }

        if (index <= 35)
        {
            super.setItem(index - 27, stack);
            return;
        }

        switch (index)
        {
            case 36:
                super.setItem(9 * 4 + 3, stack);
                return;

            case 37:
                super.setItem(9 * 4 + 2, stack);
                return;

            case 38:
                super.setItem(9 * 4 + 1, stack);
                return;

            case 39:
                super.setItem(9 * 4, stack);
                return;

            case 44:
                super.setItem(9 * 4 + 4, stack);
        }
    }
}