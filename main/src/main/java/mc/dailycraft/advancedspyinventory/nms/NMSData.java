package mc.dailycraft.advancedspyinventory.nms;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public abstract class NMSData {
    protected final UUID playerUuid;

    public NMSData(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(playerUuid);
    }

    public abstract int getInt(String id);

    public abstract void putInt(String id, int value);

    public abstract float getFloat(String id);

    public abstract void putFloat(String id, float value);

    public abstract String getString(String id);

    public abstract List<Double> getDoubleList(String id);

    public abstract List<Float> getFloatList(String id);

    public abstract ItemStack[] getArray(String id, int size, Function<Integer, Integer> slotConversion);

    public abstract void setInArray(String id, int slot, ItemStack stack);

    public abstract float getMaxHealth();

    public abstract void setMaxHealth(float maxHealth);
}