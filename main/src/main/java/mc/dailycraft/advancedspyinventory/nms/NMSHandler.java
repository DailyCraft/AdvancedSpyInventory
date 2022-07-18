package mc.dailycraft.advancedspyinventory.nms;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface NMSHandler {
    default String worldId(World world) {
        if (Main.VERSION >= 19)
            return world.getKey().getKey();
        else
            return world.getName();
    }

    NMSData getData(UUID playerUuid);

    Inventory createInventory(BaseInventory inventory);

    default void openInventory(Player player, InventoryView view) {
        player.openInventory(view);
    }

    <T extends Number> void signInterface(CustomInventoryView view, String formatKey, T defaultValue, T minimumValue, T maximumValue, Function<String, T> stringToT, Predicate<T> runAfter);

    default <T extends Number> void signInterface(CustomInventoryView view, String formatKey, T defaultValue, T minimumValue, T maximumValue, Function<String, T> stringToT, Consumer<T> runAfter) {
        signInterface(view, formatKey, defaultValue, minimumValue, maximumValue, stringToT, t -> {
            runAfter.accept(t);
            return true;
        });
    }

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