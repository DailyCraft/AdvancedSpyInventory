package mc.dailycraft.advancedspyinventory.nms;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.Triplet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface NMSHandler {
    // 1.16-1.18 - Method not implemented in Bukkit.
    default NamespacedKey worldKey(World world) {
        if (Main.VERSION > 18) // 1.19+ - Method implemented
            return world.getKey();
        else // 1.15- - Worlds not saved with a key
            return NamespacedKey.minecraft(world.getName());
    }

    // To use CraftItemStack#asCraftMirror instead of CraftItemStack#asBukkitCopy
    // 1.14- - Method not implemented in Bukkit
    ItemStack getEquipment(LivingEntity entity, EquipmentSlot slot);

    NMSData getData(UUID playerUuid);

    Inventory createInventory(BaseInventory inventory);

    default void openInventory(Player player, InventoryView view) {
        player.openInventory(view);
    }

    Triplet<?> openSign(Player player, Location loc);

    default String[] getVillagerProfessions() {
        return null;
    }

    // 1.13- - The method does not have the same name.
    default String getVillagerProfession(Villager villager) {
        return null;
    }

    // 1.13- - The method does not have the same name
    default void setVillagerProfession(Villager villager, String profession) {
    }

    // 1.14+ - Get the job site of villagers
    default Material getVillagerProfessionMaterial(Villager.Profession profession) {
        return null;
    }

    // 1.14-1.16 - Method not implemented in Bukkit
    default boolean isOcelotTrusting(Ocelot ocelot) {
        return ocelot.isTrusting();
    }

    // 1.14-1.16 - Method not implemented in Bukkit
    default void setOcelotTrusting(Ocelot ocelot, boolean trusting) {
        ocelot.setTrusting(trusting);
    }

    // 1.16- - Method not implemented in Bukkit
    default void dropItem(Player player, boolean dropAll) {
        player.dropItem(dropAll);
    }
}