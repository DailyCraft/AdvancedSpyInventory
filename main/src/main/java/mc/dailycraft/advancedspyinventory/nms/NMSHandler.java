package mc.dailycraft.advancedspyinventory.nms;

import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public interface NMSHandler {
    String worldId(World world);

    NMSData getData(UUID playerUuid);

    Inventory containerToInventory(NMSContainer container);

    <T extends Number> void signInterface(CustomInventoryView view, String formatKey, T defaultValue, T minimumValue, T maximumValue, Function<String, T> stringToT, Consumer<T> runAfter);

    Material getVillagerProfessionMaterial(Villager.Profession profession);
}