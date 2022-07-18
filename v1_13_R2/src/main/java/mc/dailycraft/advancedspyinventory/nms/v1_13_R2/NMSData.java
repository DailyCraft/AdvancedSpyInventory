package mc.dailycraft.advancedspyinventory.nms.v1_13_R2;

import mc.dailycraft.advancedspyinventory.Main;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        NBTTagCompound data = getData();
        data.setInt(id, value);
        saveData(data);
    }

    @Override
    public long getLong(String id) {
        return getData().getLong(id);
    }

    @Override
    public float getFloat(String id) {
        return getData().getFloat(id);
    }

    @Override
    public void putFloat(String id, float value) {
        NBTTagCompound data = getData();
        data.setFloat(id, value);
        saveData(data);
    }

    @Override
    public String getString(String id) {
        return getData().getString(id);
    }

    @Override
    public List<Double> getDoubleList(String id) {
        return getData().getList(id, CraftMagicNumbers.NBT.TAG_DOUBLE).stream().map(tag -> ((NBTTagDouble) tag).asDouble()).collect(Collectors.toList());
    }

    @Override
    public List<Float> getFloatList(String id) {
        return getData().getList(id, CraftMagicNumbers.NBT.TAG_FLOAT).stream().map(tag -> ((NBTTagFloat) tag).asFloat()).collect(Collectors.toList());
    }

    @Override
    public ItemStack[] getArray(String id, int size, Function<Integer, Integer> slotConversion) {
        ItemStack[] array = new ItemStack[size];
        Arrays.fill(array, new ItemStack(Material.AIR));

        getData().getList(id, CraftMagicNumbers.NBT.TAG_COMPOUND).stream().map(tag -> (NBTTagCompound) tag)
                .forEach(tag -> array[slotConversion.apply((int) tag.getByte("Slot"))] = CraftItemStack.asBukkitCopy(net.minecraft.server.v1_13_R2.ItemStack.a(tag)));

        return array;
    }

    @Override
    public void setInArray(String id, int slot, ItemStack stack) {
        NBTTagCompound data = getData();

        NBTTagList list = data.getList(id, CraftMagicNumbers.NBT.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            if (list.getCompound(i).getByte("Slot") == slot) {
                list.remove(i);
                break;
            }
        }

        NBTTagCompound tag = CraftItemStack.asNMSCopy(stack).save(new NBTTagCompound());
        tag.setByte("Slot", (byte) slot);
        list.add(tag);
        saveData(data);
    }

    @Override
    public float getMaxHealth() {
        for (NBTBase tag : getData().getList("Attributes", 10))
            if (((NBTTagCompound) tag).getString("Name").equals("minecraft:generic.max_health"))
                return ((NBTTagCompound) tag).getFloat("Base");

        return -1;
    }

    @Override
    public void setMaxHealth(float maxHealth) {
        NBTTagCompound data = getData();
        NBTTagList list = data.getList("Attributes", 10);

        for (NBTBase nbt : list) {
            if (((NBTTagCompound) nbt).getString("Name").equals("minecraft:generic.max_health")) {
                list.remove(nbt);
                break;
            }
        }

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("Name", "minecraft:generic.max_health");
        nbt.setFloat("Base", maxHealth);
        list.add(nbt);
        saveData(data);
    }

    private NBTTagCompound getData() {
        return ((WorldNBTStorage) ((CraftServer) Bukkit.getServer()).getHandle().playerFileData).getPlayerData(playerUuid.toString());
    }

    private void saveData(NBTTagCompound data) {
        if (getOfflinePlayer().isOnline())
            ((CraftPlayer) getOfflinePlayer().getPlayer()).getHandle().save(data);
        else {
            File playerDir = ((WorldNBTStorage) ((CraftServer) Bukkit.getServer()).getHandle().playerFileData).getPlayerDir();

            try {
                File file = new File(playerDir, playerUuid + ".dat.tmp");
                File file1 = new File(playerDir, playerUuid + ".dat");
                NBTCompressedStreamTools.a(data, new FileOutputStream(file));

                if (file1.exists())
                    file1.delete();

                file.renameTo(file1);
            } catch (Exception exception) {
                Main.getInstance().getLogger().severe("Failed to save player data for " + playerUuid);
                exception.printStackTrace();
            }
        }
    }
}