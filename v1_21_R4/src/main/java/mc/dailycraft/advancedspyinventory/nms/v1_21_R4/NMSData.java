package mc.dailycraft.advancedspyinventory.nms.v1_21_R4;

import mc.dailycraft.advancedspyinventory.Main;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R4.CraftServer;
import org.bukkit.craftbukkit.v1_21_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R4.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;

public class NMSData extends mc.dailycraft.advancedspyinventory.nms.NMSData {
    public NMSData(UUID playerUuid) {
        super(playerUuid);
    }

    @Override
    public int getInt(String id) {
        return getData().getIntOr(id, 0);
    }

    @Override
    public void putInt(String id, int value) {
        CompoundTag data = getData();
        data.putInt(id, value);
        saveData(data);
    }

    @Override
    public long getLong(String id) {
        return getData().getLongOr(id, 0);
    }

    @Override
    public void putLong(String id, long value) {
        CompoundTag data = getData();
        data.putLong(id, value);
        saveData(data);
    }

    @Override
    public float getFloat(String id) {
        return getData().getFloatOr(id, 0);
    }

    @Override
    public void putFloat(String id, float value) {
        CompoundTag data = getData();
        data.putFloat(id, value);
        saveData(data);
    }

    @Override
    public String getString(String id) {
        return getData().getString(id).orElse("");
    }

    @Override
    public void putString(String id, String value) {
        CompoundTag data = getData();
        data.putString(id, value);
        saveData(data);
    }

    @Override
    public double[] getList(String id) {
        return ((ListTag) getData().get(id)).stream().mapToDouble(t -> t.asDouble().orElse(0.0)).toArray();
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
    public ItemStack[] getArray(String id, int size, Function<Integer, Integer> slotConversion) {
        ItemStack[] array = new ItemStack[size];
        Arrays.fill(array, new ItemStack(Material.AIR));

        getData().getListOrEmpty(id).stream().map(tag -> (CompoundTag) tag)
                .forEach(tag -> array[slotConversion.apply((int) tag.getByteOr("Slot", (byte) 0))] = CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.parse(registryAccess(), tag).orElse(net.minecraft.world.item.ItemStack.EMPTY)));

        return array;
    }

    @Override
    public void setInArray(String id, int slot, ItemStack stack) {
        CompoundTag data = getData();

        ListTag list = data.getListOrEmpty(id);

        for (int i = 0; i < list.size(); i++) {
            if (list.getCompoundOrEmpty(i).getByteOr("Slot", (byte) 0) == slot) {
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
        for (Tag tag : getData().getListOrEmpty("attributes"))
            if (((CompoundTag) tag).getStringOr("id", "").equals("minecraft:max_health"))
                return ((CompoundTag) tag).getFloatOr("base", 0);

        return -1;
    }

    @Override
    public void setMaxHealth(float maxHealth) {
        CompoundTag data = getData();
        ListTag list = data.getListOrEmpty("attributes");

        for (Tag nbt : list) {
            if (((CompoundTag) nbt).getStringOr("id", "").equals("minecraft:max_health")) {
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