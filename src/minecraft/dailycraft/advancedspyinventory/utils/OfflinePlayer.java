package minecraft.dailycraft.advancedspyinventory.utils;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("AdvancedSpyInventory:OfflinePlayer")
public class OfflinePlayer implements ConfigurationSerializable
{
    private final Location location;
    private final double health;
    private final double maxHealth;
    private final int experience;
    private final int food;
    private final ItemStack[] inventory;
    private final ItemStack[] enderChest;

    public OfflinePlayer(Location location, double health, double maxHealth, int experience, int food, ItemStack[] inventory, ItemStack[] enderChest)
    {
        this.location = location;
        this.health = health;
        this.maxHealth = maxHealth;
        this.experience = experience;
        this.food = food;
        this.inventory = inventory;
        this.enderChest = enderChest;
    }

    @SuppressWarnings({"unused", "unchecked"})
    public OfflinePlayer(Map<String, Object> args)
    {
        location = (Location) args.get("location");
        health = (double) args.get("health");
        maxHealth = (double) args.get("maxHealth");
        experience = (int) args.get("experience");
        food = (int) args.get("food");
        inventory = ((List<ItemStack>) args.get("inventory")).toArray(new ItemStack[0]);
        enderChest = ((List<ItemStack>) args.get("enderchest")).toArray(new ItemStack[0]);
    }

    public Location getLocation()
    {
        return location;
    }

    public double getHealth()
    {
        return health;
    }

    public double getMaxHealth()
    {
        return maxHealth;
    }

    public int getExperience()
    {
        return experience;
    }

    public int getFood()
    {
        return food;
    }

    public ItemStack[] getInventory()
    {
        return inventory;
    }

    public ItemStack[] getEnderChest()
    {
        return enderChest;
    }

    @Override
    public Map<String, Object> serialize()
    {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("location", getLocation());
        result.put("health", getHealth());
        result.put("maxHealth", getMaxHealth());
        result.put("experience", getExperience());
        result.put("food", getFood());
        result.put("inventory", getInventory());
        result.put("enderchest", getEnderChest());

        return result;
    }

    @SerializableAs("AdvancedSpyInventory:Location")
    public static class Location implements ConfigurationSerializable
    {
        private final String world;
        private final double x;
        private final double y;
        private final double z;

        public Location(String world, double x, double y, double z)
        {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @SuppressWarnings("unused")
        public Location(Map<String, Object> args)
        {
            world = (String) args.get("world");
            x = (double) args.get("x");
            y = (double) args.get("y");
            z = (double) args.get("z");
        }

        public String getWorld()
        {
            return world;
        }

        public double getX()
        {
            return x;
        }

        public double getY()
        {
            return y;
        }

        public double getZ()
        {
            return z;
        }

        @Override
        public Map<String, Object> serialize()
        {
            Map<String, Object> result = new LinkedHashMap<>();

            result.put("world", getWorld());
            result.put("x", getX());
            result.put("y", getY());
            result.put("z", getZ());

            return result;
        }
    }
}