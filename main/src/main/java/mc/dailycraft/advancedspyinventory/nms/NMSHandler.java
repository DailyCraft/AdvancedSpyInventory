package mc.dailycraft.advancedspyinventory.nms;

import io.netty.channel.ChannelPipeline;
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
import java.util.function.Function;

public interface NMSHandler {
    default NamespacedKey worldKey(World world) {
        if (Main.VERSION > 18)
            return world.getKey();
        else
            return NamespacedKey.minecraft(world.getName());
    }

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

    default String getVillagerProfession(Villager villager) {
        return null;
    }

    default void setVillagerProfession(Villager villager, String profession) {
    }

    default Material getVillagerProfessionMaterial(Villager.Profession profession) {
        return null;
    }

    default boolean isOcelotTrusting(Ocelot ocelot) {
        return ocelot.isTrusting();
    }

    default void setOcelotTrusting(Ocelot ocelot, boolean trusting) {
        ocelot.setTrusting(trusting);
    }

    default void dropItem(Player player, boolean dropAll) {
        player.dropItem(dropAll);
    }
}