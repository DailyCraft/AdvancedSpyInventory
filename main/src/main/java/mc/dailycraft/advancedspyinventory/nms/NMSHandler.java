package mc.dailycraft.advancedspyinventory.nms;

import com.mojang.authlib.GameProfile;
import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.ResourceKey;
import mc.dailycraft.advancedspyinventory.utils.Triplet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;

import java.util.UUID;

public interface NMSHandler {
    // 1.16-1.18 - Method not implemented in Bukkit.
    default ResourceKey worldKey(World world) {
        if (Main.VERSION > 18) // 1.19+ - Method implemented
            return new ResourceKey(world.getKey());
        else // 1.15- - Worlds not saved with a key
            return ResourceKey.minecraft(world.getName());
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

    // 1.11- - Method not implemented in Bukkit
    default String getPlayerLocale(Player player) {
        return player.getLocale();
    }

    // 1.11- - Method not implemented in Bukkit
    default Entity getEntity(UUID uuid) {
        return Bukkit.getEntity(uuid);
    }

    // 1.20.3+ - Unwanted warning message
    default void setHeadSerializedProfile(SkullMeta meta, GameProfile profile) {}

    // 1.20.3+ - New API
    default void setBasePotionType(PotionMeta meta, PotionType potionType) {
        meta.setBasePotionType(potionType);
    }
}