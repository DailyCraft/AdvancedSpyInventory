package mc.dailycraft.advancedspyinventory.nms.v1_13_R1;

import mc.dailycraft.advancedspyinventory.Main;
import net.minecraft.server.v1_13_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R1.CraftServer;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_13_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;

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
    public void putLong(String id, long value) {
        NBTTagCompound data = getData();
        data.setLong(id, value);
        saveData(data);
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
    public void putString(String id, String value) {
        NBTTagCompound data = getData();
        data.setString(id, value);
        saveData(data);
    }

    @Override
    public double[] getList(String id) {
        return ((NBTTagList) getData().get(id)).stream().mapToDouble(t -> ((NBTNumber) t).asDouble()).toArray();
    }

    @Override
    public void putList(String id, double[] value, boolean isFloat) {
        NBTTagCompound data = getData();
        NBTTagList tag = new NBTTagList();

        for (double v : value)
            tag.add(isFloat ? new NBTTagFloat((float) v) : new NBTTagDouble(v));

        data.set(id, tag);
        saveData(data);
    }

    @Override
    public ItemStack[] getArray(String id, int size, Function<Integer, Integer> slotConversion) {
        ItemStack[] array = new ItemStack[size];
        Arrays.fill(array, new ItemStack(Material.AIR));

        getData().getList(id, CraftMagicNumbers.NBT.TAG_COMPOUND).stream().map(tag -> (NBTTagCompound) tag)
                .forEach(tag -> array[slotConversion.apply((int) tag.getByte("Slot"))] = CraftItemStack.asBukkitCopy(net.minecraft.server.v1_13_R1.ItemStack.a(tag)));

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
        for (NBTBase tag : getData().getList("Attributes", CraftMagicNumbers.NBT.TAG_COMPOUND))
            if (((NBTTagCompound) tag).getString("Name").equals("generic.maxHealth"))
                return ((NBTTagCompound) tag).getFloat("Base");

        return -1;
    }

    @Override
    public void setMaxHealth(float maxHealth) {
        NBTTagCompound data = getData();
        NBTTagList list = data.getList("Attributes", CraftMagicNumbers.NBT.TAG_COMPOUND);

        for (NBTBase nbt : list) {
            if (((NBTTagCompound) nbt).getString("Name").equals("generic.maxHealth")) {
                list.remove(nbt);
                break;
            }
        }

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("Name", "generic.maxHealth");
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