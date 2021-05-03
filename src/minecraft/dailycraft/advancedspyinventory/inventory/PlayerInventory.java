package minecraft.dailycraft.advancedspyinventory.inventory;

import minecraft.dailycraft.advancedspyinventory.utils.Config;
import minecraft.dailycraft.advancedspyinventory.utils.Permissions;
import minecraft.dailycraft.advancedspyinventory.utils.TranslationUtils;
import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static minecraft.dailycraft.advancedspyinventory.inventory.InventoryUtils.*;

public class PlayerInventory extends AbstractInventory
{
    private final InventoryUtils.OfflinePlayer target;
    
    public PlayerInventory(Player sender, UUID target)
    {
        super(new TranslationUtils(sender), 6);
        this.target = new InventoryUtils.OfflinePlayer(target);
    }

    @Override
    public List<ItemStack> getContents()
    {
        if (target.isOnline())
            return ((CraftPlayer) target.getOnlinePlayer()).getHandle().inventory.getContents();
        else
        {
            Config.reload();
            List<ItemStack> result = new ArrayList<>();
            Arrays.stream(target.getOfflinePlayer().getInventory()).forEach(stack -> result.add(CraftItemStack.asNMSCopy(stack)));
            return result;
        }
    }

    @Override
    public ItemStack getItem(int index)
    {
        if (index <= 26)
        {
            return getContents().get(index + 9);
        }
        else if (index <= 35)
        {
            return getContents().get(index - 27);
        }
        else if (index <= 39)
        {
            return InformationItems.getItem(getContents().get(9 * 4 + -(index - 39)), informationItems.items()[-(index - 41)]);
        }

        switch (index)
        {
            case 44:
                return InformationItems.getItem(getContents().get(9 * 4 + 4), informationItems.offHand);

            case 46:
                return setPotionWithDisplayName(PotionType.INSTANT_HEAL, translation.format("interface.inventory.health", new DecimalFormat(".#").format(target.isOnline() ? target.getOnlinePlayer().getHealth() : target.getOfflinePlayer().getHealth()), target.isOnline() ? target.getOnlinePlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() : target.getOfflinePlayer().getMaxHealth()));

            case 47:
                if (translation.getSender().hasPermission(Permissions.INVENTORY_LOCATION.get()))
                {
                    return setItemWithDisplayName(Material.ARROW, translation.format("interface.inventory.location"),
                            translation.format("interface.inventory.world", target.isOnline() ? target.getOnlinePlayer().getWorld().getName() : target.getOfflinePlayer().getLocation().getWorld(), target.isOnline() ? target.getOnlinePlayer().getWorld().getEnvironment().name() : Bukkit.getWorld(target.getOfflinePlayer().getLocation().getWorld()).getEnvironment().name()),
                            "X : " + new DecimalFormat(".##").format(target.isOnline() ? target.getOnlinePlayer().getLocation().getX() : target.getOfflinePlayer().getLocation().getX()),
                            "Y : " + new DecimalFormat(".##").format(target.isOnline() ? target.getOnlinePlayer().getLocation().getY() : target.getOfflinePlayer().getLocation().getY()),
                            "Z : " + new DecimalFormat(".##").format(target.isOnline() ? target.getOnlinePlayer().getLocation().getZ() : target.getOfflinePlayer().getLocation().getZ()), "",
                            "-> " + translation.format("interface.inventory.teleport"));
                }

                break;

            case 49:
                return setItemWithDisplayName(Material.BARRIER, translation.format("interface.inventory.clear"));

            case 51:
                return setItemWithDisplayName(Material.EXP_BOTTLE, translation.format("interface.inventory.experience", target.isOnline() ? target.getOnlinePlayer().getTotalExperience() : target.getOfflinePlayer().getExperience()));

            case 52:
                return setItemWithDisplayName(Material.COOKED_BEEF, translation.format("interface.inventory.food", target.isOnline() ? target.getOnlinePlayer().getFoodLevel() : target.getOfflinePlayer().getFood()));

            case 53:
                return setItemWithDisplayName(Material.ENDER_CHEST, translation.format("interface.inventory.enderchest"), Bukkit.getOfflinePlayer(target.getUuid()).getName());
        }

        return getVoidItem();
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        if (index <= 26)
        {
            set(index + 9, stack);
        }
        else if (index <= 35)
        {
            set(index - 27, stack);
        }
        else if (index <= 39)
        {
            if (!stack.equals(informationItems.items()[-(index - 41)]))
                set(9 * 4 + -(index - 39), stack);
        }
        else if (index == 44)
        {
            if (!stack.equals(informationItems.offHand))
                set(9 * 4 + 4, stack);
        }
    }

    @Override
    public String getName()
    {
        return translation.format("interface.inventory.name", Bukkit.getOfflinePlayer(target.getUuid()).getName());
    }

    private void set(int index, ItemStack stack)
    {
        if (target.isOnline())
        {
            getContents().set(index, stack);
            target.getOnlinePlayer().getInventory().setItem(index, CraftItemStack.asBukkitCopy(stack));
        }
        else
        {
            getContents().set(index, stack);
            target.getOfflinePlayer().getInventory()[index] = CraftItemStack.asBukkitCopy(stack);
            Config.save();
        }
    }
}