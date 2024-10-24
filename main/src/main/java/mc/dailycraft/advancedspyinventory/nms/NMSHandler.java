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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public interface NMSHandler {

    // 1.16-1.18 - Method not implemented in Bukkit.
    default ResourceKey worldKey(World world) {
        if (Main.VERSION >= 19) // 1.19+ - Method implemented
            return new ResourceKey(world.getKey());
        else // 1.15- - Worlds not saved with a key
            return ResourceKey.minecraft(world.getName());
    }

    // To use CraftItemStack#asCraftMirror instead of CraftItemStack#asBukkitCopy
    // 1.14- - Method not implemented in Bukkit
    ItemStack getEquipment(LivingEntity entity, EquipmentSlot slot);

    NMSData getData(UUID playerUuid);

    Inventory createInventory(BaseInventory inventory);

    // 1.21+ - Extends a CraftBukkit class
    default InventoryView createView(Player viewer, BaseInventory inventory) {
        if (Variables.VIEW_CONSTRUCTOR == null) {
            String version;

            if (Main.VERSION >= 20)
                version = "v1_20_R1";
            else if (Main.VERSION >= 14)
                version = "v1_14_R1";
            else
                version = "v1_11_R1";

            try {
                Variables.VIEW_CONSTRUCTOR = (Constructor<? extends InventoryView>) Class.forName("mc.dailycraft.advancedspyinventory.nms." + version + ".CustomInventoryView")
                        .getConstructor(Player.class, BaseInventory.class);
            } catch (NoSuchMethodException | ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }
        }

        try {
            return Variables.VIEW_CONSTRUCTOR.newInstance(viewer, inventory);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

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

    // 1.20.1-1.20.4 - Unwanted warning message
    // 1.21.2+ - The variable type is no longer a GameProfile.
    default void setHeadProfile(SkullMeta meta, GameProfile profile) throws ReflectiveOperationException {
        Variables.setProfileField(meta, profile);
    }

    // 1.20.3+ - New API
    default void setBasePotionType(PotionMeta meta, PotionType potionType) {
        meta.setBasePotionType(potionType);
    }

    class Variables {
        private static Constructor<? extends InventoryView> VIEW_CONSTRUCTOR;
        private static Field PROFILE_FIELD;

        public static void setProfileField(SkullMeta meta, Object profile) throws ReflectiveOperationException {
            if (PROFILE_FIELD == null)
                (PROFILE_FIELD = meta.getClass().getDeclaredField("profile")).setAccessible(true);

            PROFILE_FIELD.set(meta, profile);
        }
    }
}