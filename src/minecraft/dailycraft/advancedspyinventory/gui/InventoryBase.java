package minecraft.dailycraft.advancedspyinventory.gui;

import minecraft.dailycraft.advancedspyinventory.ConfigsManager;
import minecraft.dailycraft.advancedspyinventory.TranslationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public abstract class InventoryBase implements Inventory
{
    public final TranslationUtils translation;

    private final ConfigsManager configsManager;
    private final FileConfiguration playersConfig;
    private final String sectionName;

    public final UUID uuid;

    public InventoryBase(Player sender, UUID uuid, JavaPlugin plugin, String sectionName)
    {
        translation = new TranslationUtils(sender);

        configsManager = new ConfigsManager(plugin);
        playersConfig = configsManager.getOfflinePlayersConfig();
        this.sectionName = sectionName;

        this.uuid = uuid;
    }

    @Override
    public int getMaxStackSize()
    {
        return 64;
    }

    @Override
    public void setMaxStackSize(int size)
    {}

    @Override
    public ItemStack getItem(int index)
    {
        if (Bukkit.getOfflinePlayer(uuid).isOnline())
        {
            if (sectionName.equals("inventory"))
                return getOnlinePlayer().getInventory().getItem(index);
            else
                return getOnlinePlayer().getEnderChest().getItem(index);
        }
        else
        {
            try
            {
                configsManager.reloadConfig();
                return getContents()[index];
            }
            catch (NullPointerException exception)
            {
                ItemStack[] stacks = new ItemStack[9 * 4 + 5];

                stacks[index] = null;

                return stacks[index];
            }
        }
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        if (Bukkit.getOfflinePlayer(uuid).isOnline())
        {
            if (sectionName.equals("inventory"))
                getOnlinePlayer().getInventory().setItem(index, stack);
            else
                getOnlinePlayer().getEnderChest().setItem(index, stack);
        }
        else
        {
            if (stack.equals(new ItemStack(Material.AIR)))
                ((ArrayList<ItemStack>) playersConfig.get(uuid + "." + sectionName)).set(index, null);
            else
                ((ArrayList<ItemStack>) playersConfig.get(uuid + "." + sectionName)).set(index, stack);

            configsManager.saveConfig();
        }
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... stacks) throws IllegalArgumentException
    {
        return null;
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... stacks) throws IllegalArgumentException
    {
        return null;
    }

    @Override
    public ItemStack[] getContents()
    {
        if (Bukkit.getOfflinePlayer(uuid).isOnline())
        {
            if (sectionName.equals("inventory"))
                return getOnlinePlayer().getInventory().getContents();
            else
                return getOnlinePlayer().getEnderChest().getContents();
        }

        return ((ArrayList<ItemStack>) playersConfig.get(uuid + "." + sectionName)).toArray(new ItemStack[9 * 4 + 5]);
    }

    @Override
    public void setContents(ItemStack[] stacks) throws IllegalArgumentException
    {
        playersConfig.set(uuid + "." + sectionName, stacks);
    }

    @Override
    public ItemStack[] getStorageContents()
    {
        return new ItemStack[0];
    }

    @Override
    public void setStorageContents(ItemStack[] stacks) throws IllegalArgumentException
    {}

    @Deprecated
    @Override
    public boolean contains(int materialId)
    {
        return contains(Material.getMaterial(materialId));
    }

    @Override
    public boolean contains(Material material) throws IllegalArgumentException
    {
        return contains(new ItemStack(material));
    }

    @Override
    public boolean contains(ItemStack stack)
    {
        return Arrays.asList(getContents()).contains(stack);
    }

    @Deprecated
    @Override
    public boolean contains(int materialId, int amount)
    {
        return contains(new ItemStack(Material.getMaterial(materialId), amount));
    }

    @Override
    public boolean contains(Material material, int amount) throws IllegalArgumentException
    {
        return contains(new ItemStack(material), amount);
    }

    @Override
    public boolean contains(ItemStack stack, int amount)
    {
        stack.setAmount(amount);

        return Arrays.asList(getContents()).contains(stack);
    }

    @Override
    public boolean containsAtLeast(ItemStack stack, int amount)
    {
        return false;
    }

    @Deprecated
    @Override
    public HashMap<Integer, ? extends ItemStack> all(int materialId)
    {
        return null;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException
    {
        return null;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack stack)
    {
        return null;
    }

    @Deprecated
    @Override
    public int first(int materialId)
    {
        return first(Material.getMaterial(materialId));
    }

    @Override
    public int first(Material material) throws IllegalArgumentException
    {
        return first(new ItemStack(material));
    }

    @Override
    public int first(ItemStack stack)
    {
        for (int i = 0; i < getContents().length; ++i)
        {
            if (getContents()[i] == stack)
                return i;
        }

        return -1;
    }

    @Override
    public int firstEmpty()
    {
        for (int i = 0; i < getContents().length; ++i)
        {
            if (getContents()[i] == null)
                return i;
        }

        return -1;
    }

    @Deprecated
    @Override
    public void remove(int materialId)
    {
        remove(Material.getMaterial(materialId));
    }

    @Override
    public void remove(Material material) throws IllegalArgumentException
    {
        remove(new ItemStack(material));
    }

    @Override
    public void remove(ItemStack stack)
    {
        Arrays.asList(getContents()).remove(stack);
    }

    @Override
    public void clear(int index)
    {
        Arrays.asList(getContents()).remove(index);
    }

    @Override
    public void clear()
    {
        Arrays.asList(getContents()).clear();
    }

    @Override
    public List<HumanEntity> getViewers()
    {
        return null;
    }

    @Override
    public String getTitle()
    {
        return getName();
    }

    @Override
    public InventoryType getType()
    {
        return InventoryType.CHEST;
    }

    @Override
    public InventoryHolder getHolder()
    {
        return null;
    }

    @Override
    public ListIterator<ItemStack> iterator()
    {
        return null;
    }

    @Override
    public ListIterator<ItemStack> iterator(int index)
    {
        return null;
    }

    @Override
    public Location getLocation()
    {
        return getOnlinePlayer().getLocation();
    }

    private Player getOnlinePlayer()
    {
        return Bukkit.getPlayer(uuid);
    }

    public double getLocation(String name)
    {
        if (Bukkit.getOfflinePlayer(uuid).isOnline())
        {
            switch (name)
            {
                case "x":
                    return getLocation().getX();

                case "y":
                    return getLocation().getY();

                case "z":
                    return getLocation().getZ();
            }
        }

        return playersConfig.getDouble(uuid + ".location." + name);
    }

    public String getWorldName()
    {
        if (Bukkit.getOfflinePlayer(uuid).isOnline())
            return getLocation().getWorld().getName();

        return playersConfig.getString(uuid + ".location.world");
    }

    public double getHealth()
    {
        if (Bukkit.getOfflinePlayer(uuid).isOnline())
            return getOnlinePlayer().getHealth();

        return playersConfig.getDouble(uuid + ".health");
    }

    public int getExperience()
    {
        if (Bukkit.getOfflinePlayer(uuid).isOnline())
            return getOnlinePlayer().getTotalExperience();

        return playersConfig.getInt(uuid + ".experience");
    }

    public int getFood()
    {
        if (Bukkit.getOfflinePlayer(uuid).isOnline())
            return getOnlinePlayer().getFoodLevel();

        return playersConfig.getInt(uuid + ".food");
    }
}