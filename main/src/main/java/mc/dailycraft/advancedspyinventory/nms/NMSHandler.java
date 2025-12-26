package mc.dailycraft.advancedspyinventory.nms;

import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.Triplet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface NMSHandler {
    // To use CraftItemStack#asCraftMirror instead of CraftItemStack#asBukkitCopy
    ItemStack getEquipment(LivingEntity entity, EquipmentSlot slot);

    NMSData getData(UUID playerUuid);

    Inventory createInventory(BaseInventory inventory);

    InventoryView createView(Player viewer, BaseInventory inventory);

    default void openInventory(Player player, InventoryView view) {
        player.openInventory(view);
    }

    Triplet<?> openSign(Player player, Location loc);

    // 1.14+ - Get the job site of villagers
    default Material getVillagerProfessionMaterial(Villager.Profession profession) {
        return null;
    }
}