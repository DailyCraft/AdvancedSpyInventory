package minecraft.dailycraft.advancedspyinventory.inventory;

import minecraft.dailycraft.advancedspyinventory.utils.Config;
import minecraft.dailycraft.advancedspyinventory.utils.TranslationUtils;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.*;

public class EnderChestInventory extends AbstractInventory
{
    private final InventoryUtils.OfflinePlayer target;

    public EnderChestInventory(Player sender, UUID target)
    {
        super(new TranslationUtils(sender), 4);

        this.target = new InventoryUtils.OfflinePlayer(target);
    }

    @Override
    public List<ItemStack> getContents()
    {
        if (target.isOnline())
            return ((CraftPlayer) target.getOnlinePlayer()).getHandle().getEnderChest().getContents();
        else
        {
            List<ItemStack> result = new ArrayList<>();
            Config.reload();
            Arrays.stream(target.getOfflinePlayer().getEnderChest()).forEach(stack -> result.add(CraftItemStack.asNMSCopy(stack)));
            return result;
        }
    }

    @Override
    public synchronized ItemStack getItem(int index)
    {
        if (index < getSize() - 9)
            return getContents().get(index);
        else if (index == getSize() - 9)
            return InventoryUtils.setItemWithDisplayName(Material.CHEST, translation.format("interface.enderchest.inventory"), Bukkit.getOfflinePlayer(target.getUuid()).getName());
        else if (index == getSize() - 5)
            return InventoryUtils.setItemWithDisplayName(Material.BARRIER, translation.format("interface.enderchest.clear"));
        else
            return InventoryUtils.getVoidItem();
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        if (index < getSize() - 9)
        {
            getContents().set(index, stack);

            if (!target.isOnline())
            {
                target.getOfflinePlayer().getEnderChest()[index] = CraftItemStack.asBukkitCopy(stack);
                Config.save();
            }
        }
    }

    @Override
    public void onOpen(CraftHumanEntity who)
    {
        super.onOpen(who);
        translation.getSender().playSound(translation.getSender().getLocation(), Sound.BLOCK_ENDERCHEST_OPEN, 2, 1);
    }

    @Override
    public void onClose(CraftHumanEntity who)
    {
        super.onClose(who);
        translation.getSender().playSound(translation.getSender().getLocation(), Sound.BLOCK_ENDERCHEST_CLOSE, 2, 1);
    }

    @Override
    public String getName()
    {
        return translation.format("interface.enderchest.name", Bukkit.getOfflinePlayer(target.getUuid()).getName());
    }
}