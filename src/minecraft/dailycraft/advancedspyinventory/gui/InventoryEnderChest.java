package minecraft.dailycraft.advancedspyinventory.gui;

import minecraft.dailycraft.advancedspyinventory.TranslationUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import static minecraft.dailycraft.advancedspyinventory.gui.InventoryUtils.*;

public class InventoryEnderChest implements Inventory
{
    private final Inventory playerEc;
    private final TranslationUtils translation;
    private final String playerName;

    public InventoryEnderChest(Player sender, Inventory playerEc, String playerName)
    {
        this.playerEc = playerEc;
        translation = new TranslationUtils(sender);
        this.playerName = playerName;
    }

    @Override
    public int getSize()
    {
        return playerEc.getSize() + 9;
    }

    @Override
    public int getMaxStackSize()
    {
        return playerEc.getMaxStackSize();
    }

    @Override
    public void setMaxStackSize(int amount)
    {
        playerEc.setMaxStackSize(amount);
    }

    @Override
    public String getName()
    {
        return translation.translate("" + ChatColor.DARK_RED + ChatColor.BOLD + playerName + ChatColor.RESET + ChatColor.YELLOW + "'s Ender Chest", ChatColor.YELLOW + "Coffre de l'Ender de " + ChatColor.DARK_RED + ChatColor.BOLD + playerName);
    }

    @Override
    public ItemStack getItem(int index)
    {
        if (index <= getSize() - 10)
        {
            return playerEc.getItem(index);
        }
        else if (index == getSize() - 5)
        {
            return setItemWithDisplayName(Material.BARRIER, translation.translate("Clear Ender Chest", "Vider le Coffre de l'Ender"));
        }
        else if (index >= getSize() - 9)
        {
            return getVoidItem();
        }

        return null;
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        if (index <= getSize() - 10)
        {
            playerEc.setItem(index, stack);
        }
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... stacks) throws IllegalArgumentException
    {
        return playerEc.addItem(stacks);
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... stacks) throws IllegalArgumentException
    {
        return playerEc.removeItem(stacks);
    }

    @Override
    public ItemStack[] getContents()
    {
        return playerEc.getContents();
    }

    @Override
    public void setContents(ItemStack[] stacks) throws IllegalArgumentException
    {
        playerEc.setContents(stacks);
    }

    @Override
    public ItemStack[] getStorageContents()
    {
        return getContents();
    }

    @Override
    public void setStorageContents(ItemStack[] stacks) throws IllegalArgumentException
    {
        playerEc.setStorageContents(stacks);
    }

    @Deprecated
    @Override
    public boolean contains(int materialId)
    {
        return playerEc.contains(materialId);
    }

    @Override
    public boolean contains(Material material) throws IllegalArgumentException
    {
        return playerEc.contains(material);
    }

    @Override
    public boolean contains(ItemStack stack)
    {
        return playerEc.contains(stack);
    }

    @Deprecated
    @Override
    public boolean contains(int materialId, int amount)
    {
        return playerEc.contains(materialId, amount);
    }

    @Override
    public boolean contains(Material material, int amount) throws IllegalArgumentException
    {
        return playerEc.contains(material, amount);
    }

    @Override
    public boolean contains(ItemStack stack, int amount)
    {
        return playerEc.contains(stack, amount);
    }

    @Override
    public boolean containsAtLeast(ItemStack stack, int amount)
    {
        return playerEc.containsAtLeast(stack, amount);
    }

    @Deprecated
    @Override
    public HashMap<Integer, ? extends ItemStack> all(int materialId)
    {
        return playerEc.all(materialId);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException
    {
        return playerEc.all(material);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack stack)
    {
        return playerEc.all(stack);
    }

    @Deprecated
    @Override
    public int first(int materialId)
    {
        return playerEc.first(materialId);
    }

    @Override
    public int first(Material material) throws IllegalArgumentException
    {
        return playerEc.first(material);
    }

    @Override
    public int first(ItemStack stack)
    {
        return playerEc.first(stack);
    }

    @Override
    public int firstEmpty()
    {
        return playerEc.firstEmpty();
    }

    @Deprecated
    @Override
    public void remove(int materialId)
    {
        playerEc.remove(materialId);
    }

    @Override
    public void remove(Material material) throws IllegalArgumentException
    {
        playerEc.remove(material);
    }

    @Override
    public void remove(ItemStack stack)
    {
        playerEc.remove(stack);
    }

    @Override
    public void clear(int index)
    {
        playerEc.clear(index);
    }

    @Override
    public void clear()
    {
        playerEc.clear();
    }

    @Override
    public List<HumanEntity> getViewers()
    {
        return playerEc.getViewers();
    }

    @Override
    public String getTitle()
    {
        return playerEc.getTitle();
    }

    @Override
    public InventoryType getType()
    {
        return playerEc.getType();
    }

    @Override
    public InventoryHolder getHolder()
    {
        return playerEc.getHolder();
    }

    @Override
    public ListIterator<ItemStack> iterator()
    {
        return playerEc.iterator();
    }

    @Override
    public ListIterator<ItemStack> iterator(int index)
    {
        return playerEc.iterator(index);
    }

    @Override
    public Location getLocation()
    {
        return playerEc.getLocation();
    }
}
