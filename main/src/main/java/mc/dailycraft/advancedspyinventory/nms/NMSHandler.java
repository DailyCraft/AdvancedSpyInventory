package mc.dailycraft.advancedspyinventory.nms;

import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface NMSHandler {
    String worldId(World world);

    NMSData getData(UUID playerUuid);

    Inventory createInventory(BaseInventory inventory);

    <T extends Number> void signInterface(CustomInventoryView view, String formatKey, T defaultValue, T minimumValue, T maximumValue, Function<String, T> stringToT, Predicate<T> runAfter);

    default <T extends Number> void signInterface(CustomInventoryView view, String formatKey, T defaultValue, T minimumValue, T maximumValue, Function<String, T> stringToT, Consumer<T> runAfter) {
        signInterface(view, formatKey, defaultValue, minimumValue, maximumValue, stringToT, t -> {
            runAfter.accept(t);
            return true;
        });
    }

    Material getVillagerProfessionMaterial(Villager.Profession profession);

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