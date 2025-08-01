package mc.dailycraft.advancedspyinventory.nms.v1_19_R2;

import mc.dailycraft.advancedspyinventory.Main;
import net.minecraft.Util;
import net.minecraft.nbt.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R2.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.IntUnaryOperator;

public class NMSData extends mc.dailycraft.advancedspyinventory.nms.NMSData {
    public NMSData(UUID playerUuid) {
        super(playerUuid);
    }

    @Override
    public int getInt(String id) {
        return getData().getInt(id);
    }

    @Override
    public void putInt(String id, int value) {
        CompoundTag data = getData();
        data.putInt(id, value);
        saveData(data);
    }

    @Override
    public long getLong(String id) {
        return getData().getLong(id);
    }

    @Override
    public void putLong(String id, long value) {
        CompoundTag data = getData();
        data.putLong(id, value);
        saveData(data);
    }

    @Override
    public float getFloat(String id) {
        return getData().getFloat(id);
    }

    @Override
    public void putFloat(String id, float value) {
        CompoundTag data = getData();
        data.putFloat(id, value);
        saveData(data);
    }

    @Override
    public String getString(String id) {
        return getData().getString(id);
    }

    @Override
    public void putString(String id, String value) {
        CompoundTag data = getData();
        data.putString(id, value);
        saveData(data);
    }

    @Override
    public double[] getList(String id) {
        return ((ListTag) getData().get(id)).stream().mapToDouble(t -> ((NumericTag) t).getAsDouble()).toArray();
    }

    @Override
    public void putList(String id, double[] value, boolean isFloat) {
        CompoundTag data = getData();
        ListTag tag = new ListTag();

        for (double v : value)
            tag.add(isFloat ? FloatTag.valueOf((float) v) : DoubleTag.valueOf(v));

        data.put(id, tag);
        saveData(data);
    }

    @Override
    public ItemStack[] getArray(String id, int size, IntUnaryOperator slotConversion) {
        ItemStack[] array = new ItemStack[size];
        Arrays.fill(array, new ItemStack(Material.AIR));

        getData().getList(id, CraftMagicNumbers.NBT.TAG_COMPOUND).stream().map(tag -> (CompoundTag) tag)
                .forEach(tag -> array[slotConversion.applyAsInt(tag.getByte("Slot"))] = CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.of(tag)));

        return array;
    }

    @Override
    public void setInArray(String id, int slot, ItemStack stack) {
        CompoundTag data = getData();

        ListTag list = data.getList(id, CraftMagicNumbers.NBT.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            if (list.getCompound(i).getByte("Slot") == slot) {
                list.remove(i);
                break;
            }
        }

        CompoundTag tag = CraftItemStack.asNMSCopy(stack).save(new CompoundTag());
        tag.putByte("Slot", (byte) slot);
        list.add(tag);
        saveData(data);
    }

    @Override
    public float getMaxHealth() {
        for (Tag tag : getData().getList("Attributes", CraftMagicNumbers.NBT.TAG_COMPOUND))
            if (((CompoundTag) tag).getString("Name").equals("minecraft:generic.max_health"))
                return ((CompoundTag) tag).getFloat("Base");

        return -1;
    }

    @Override
    public void setMaxHealth(float maxHealth) {
        CompoundTag data = getData();
        ListTag list = data.getList("Attributes", CraftMagicNumbers.NBT.TAG_COMPOUND);

        for (Tag nbt : list) {
            if (((CompoundTag) nbt).getString("Name").equals("minecraft:generic.max_health")) {
                list.remove(nbt);
                break;
            }
        }

        CompoundTag nbt = new CompoundTag();
        nbt.putString("Name", "minecraft:generic.max_health");
        nbt.putFloat("Base", maxHealth);
        list.add(nbt);
        saveData(data);
    }

    private CompoundTag getData() {
        return ((CraftServer) Bukkit.getServer()).getHandle().playerIo.getPlayerData(playerUuid.toString());
    }

    private void saveData(CompoundTag data) {
        if (getOfflinePlayer().isOnline())
            ((CraftPlayer) getOfflinePlayer().getPlayer()).getHandle().save(data);
        else {
            File playerDir = ((CraftServer) Bukkit.getServer()).getHandle().playerIo.getPlayerDir();

            try {
                File file = File.createTempFile(playerUuid + "-", ".dat", playerDir);
                NbtIo.writeCompressed(data, file);
                Util.safeReplaceFile(new File(playerDir, playerUuid + ".dat"), file, new File(playerDir, playerUuid + ".dat_old"));
            } catch (Exception exception) {
                Main.getInstance().getLogger().severe("Failed to save player data for " + playerUuid);
                throw new RuntimeException(exception);
            }
        }
    }
}