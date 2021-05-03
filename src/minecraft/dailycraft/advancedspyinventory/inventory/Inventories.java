package minecraft.dailycraft.advancedspyinventory.inventory;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventory;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class Inventories
{
    public static Inventory getPlayerInventory(Player sender, UUID target)
    {
        return new CraftInventory(new PlayerInventory(sender, target));
    }

    public static Inventory getEnderChest(Player sender, UUID target)
    {
        return new CraftInventory(new EnderChestInventory(sender, target));
    }

    public static Inventory getEntityInventory(Player sender, LivingEntity entity)
    {
        return new CraftInventory(new EntityInventory(sender, entity));
    }

    public static Inventory getVillagerInventory(Player sender, Villager villager)
    {
        return new CraftInventory(new VillagerInventory(sender, villager));
    }

    public static Inventory getEndermanInventory(Player sender, Enderman enderman)
    {
        return new CraftInventory(new EndermanInventory(sender, enderman));
    }

    public static Inventory getSheepInventory(Player sender, Sheep sheep)
    {
        return new CraftInventory(new SheepInventory(sender, sheep));
    }

    public static Inventory getHorseInventory(Player sender, AbstractHorse horse)
    {
        return new CraftInventory(new HorseInventory(sender, horse));
    }

    public static Inventory getDonkeyAndMuleInventory(Player sender, ChestedHorse chestedHorse)
    {
        return new CraftInventory(new DonkeyAndMuleInventory(sender, chestedHorse));
    }

    public static Inventory getLlamaInventory(Player sender, Llama llama)
    {
        return new CraftInventory(new LlamaInventory(sender, llama));
    }
}