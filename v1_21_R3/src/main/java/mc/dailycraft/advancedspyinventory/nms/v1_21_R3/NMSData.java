package mc.dailycraft.advancedspyinventory.nms.v1_21_R3;

import mc.dailycraft.advancedspyinventory.Main;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R3.CraftServer;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R3.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
                .forEach(tag -> array[slotConversion.applyAsInt(tag.getByte("Slot"))] = CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.parse(registryAccess(), tag).orElse(net.minecraft.world.item.ItemStack.EMPTY)));

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

        if (stack.getType() != Material.AIR) {
            CompoundTag tag = new CompoundTag();
            tag.putByte("Slot", (byte) slot);
            list.add(CraftItemStack.asNMSCopy(stack).save(registryAccess(), tag));
        }

        saveData(data);
    }

    private RegistryAccess registryAccess() {
        return ((CraftWorld) Bukkit.getWorlds().getFirst()).getHandle().registryAccess();
    }

    @Override
    public float getMaxHealth() {
        for (Tag tag : getData().getList("attributes", CraftMagicNumbers.NBT.TAG_COMPOUND))
            if (((CompoundTag) tag).getString("id").equals("minecraft:max_health"))
                return ((CompoundTag) tag).getFloat("base");

        return -1;
    }

    @Override
    public void setMaxHealth(float maxHealth) {
        CompoundTag data = getData();
        ListTag list = data.getList("attributes", CraftMagicNumbers.NBT.TAG_COMPOUND);

        for (Tag nbt : list) {
            if (((CompoundTag) nbt).getString("id").equals("minecraft:max_health")) {
                list.remove(nbt);
                break;
            }
        }

        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", "minecraft:max_health");
        nbt.putFloat("base", maxHealth);
        list.add(nbt);
        saveData(data);
    }

    private CompoundTag getData() {
        return ((CraftServer) Bukkit.getServer()).getHandle().playerIo.load(playerUuid.toString(), playerUuid.toString()).orElseThrow();
    }

    private void saveData(CompoundTag data) {
        if (getOfflinePlayer().isOnline())
            ((CraftPlayer) getOfflinePlayer().getPlayer()).getHandle().save(data);
        else {
            Path playerDir = ((CraftServer) Bukkit.getServer()).getHandle().playerIo.getPlayerDir().toPath();

            try {
                Path file = Files.createTempFile(playerDir, playerUuid + "-", ".dat");
                NbtIo.writeCompressed(data, file);
                Util.safeReplaceFile(playerDir.resolve(playerUuid + ".dat"), file, playerDir.resolve(playerUuid + ".dat_old"));
            } catch (IOException exception) {
                Main.getInstance().getLogger().severe("Failed to save player data for " + playerUuid);
                throw new RuntimeException(exception);
            }
        }
    }
}