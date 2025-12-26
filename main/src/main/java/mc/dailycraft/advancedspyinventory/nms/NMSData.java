package mc.dailycraft.advancedspyinventory.nms;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.IntUnaryOperator;

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

    public abstract long getLong(String id);

    public abstract void putLong(String id, long value);

    public abstract float getFloat(String id);

    public abstract void putFloat(String id, float value);

    public abstract String getString(String id);

    public abstract void putString(String id, String value);

    public abstract double[] getList(String id);

    public abstract void putList(String id, double[] value, boolean isFloat);

    public abstract ItemStack[] getArray(String id, int size, IntUnaryOperator slotConversion);

    public abstract void setInArray(String id, int slot, ItemStack stack);

    public abstract float getMaxHealth();

    public abstract void setMaxHealth(float maxHealth);

    public final ItemStack[] getInventory() {
        return getArray("Inventory", 41, i -> {
            if (i >= 100 && i <= 103)
                i -= 64;
            else if (i == -106)
                i = 40;

            return i;
        });
    }

    public final void setInInventory(int slot, ItemStack stack) {
        setInArray("Inventory", slot <= 35 ? slot : slot <= 39 ? slot + 64 : slot == 40 ? -106 : -1, stack);
    }

    public int getEquipmentIndex(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 39;
            case CHEST -> 38;
            case LEGS -> 37;
            case FEET -> 36;
            case OFF_HAND -> 40;
            default -> throw new IllegalArgumentException("Unexpected slot " + slot);
        };
    }

    public ItemStack getEquipment(EquipmentSlot slot) {
        return getInventory()[getEquipmentIndex(slot)];
    }

    public void setEquipment(EquipmentSlot slot, ItemStack stack) {
        setInInventory(getEquipmentIndex(slot), stack);
    }
}