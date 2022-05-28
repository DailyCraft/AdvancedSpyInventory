package mc.dailycraft.advancedspyinventory.utils;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerData implements AnimalTamer {
    private final UUID playerUuid;

    public PlayerData(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    @NotNull
    @Override
    public UUID getUniqueId() {
        return playerUuid;
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(playerUuid);
    }

    public Player getPlayer() {
        return getOfflinePlayer().getPlayer();
    }

    public EntityPlayer getNmsPlayer() {
        return isOnline() ? ((CraftPlayer) getPlayer()).getHandle() : null;
    }

    public boolean isOnline() {
        return getOfflinePlayer().isOnline();
    }

    @Override
    public String getName() {
        return getOfflinePlayer().getName();
    }

    public float getHealth() {
        return isOnline() ? (float) getPlayer().getHealth() : getData().getFloat("Health");
    }

    public void setHealth(float health) {
        if (isOnline())
            getPlayer().setHealth(health);
        else
            modifyData(data -> data.setFloat("Health", health));
    }

    public float getMaxHealth() {
        if (isOnline())
            return (float) getPlayer().getMaxHealth();

        for (NBTBase nbt : getData().getList("Attributes", 10))
            if (((NBTTagCompound) nbt).getString("Name").equals("minecraft:generic.max_health"))
                return ((NBTTagCompound) nbt).getFloat("Base");

        return -1;
    }

    public void setMaxHealth(float maxHealth) {
        if (isOnline())
            getPlayer().setMaxHealth(maxHealth);
        else {
            modifyData(data -> {
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
            });
        }
    }

    public Location getLocation() {
        if (isOnline())
            return getPlayer().getLocation();

        List<Double> position = getData().getList("Pos", 6).stream()
                .map(nbtBase -> ((NBTTagDouble) nbtBase).asDouble())
                .collect(Collectors.toList());

        List<Float> rotation = getData().getList("Rotation", 5).stream()
                .map(nbtBase -> ((NBTTagFloat) nbtBase).asFloat())
                .collect(Collectors.toList());

        return new Location(
                Bukkit.getWorlds().stream().filter(world -> ((CraftWorld) world).getHandle().getDimensionKey().a().toString().equals(getData().getString("Dimension"))).findFirst().orElseGet(() -> Bukkit.getWorlds().get(0)),
                position.get(0), position.get(1), position.get(2), rotation.get(0), rotation.get(1));
    }

    public int getSelectedSlot() {
        return isOnline() ? getPlayer().getInventory().getHeldItemSlot() : getData().getInt("SelectedItemSlot");
    }

    public void setSelectedSlot(int slot) {
        if (isOnline())
            getPlayer().getInventory().setHeldItemSlot(slot);

        modifyData(data -> data.setInt("SelectedItemSlot", slot));
    }

    public List<ItemStack> getInventory() {
        if (isOnline())
            return getNmsPlayer().inventory.getContents();

        List<ItemStack> result = NonNullList.a(41, ItemStack.b);

        getData().getList("Inventory", 10).stream().map(nbtBase -> (NBTTagCompound) nbtBase).forEach(nbt -> {
            byte b = nbt.getByte("Slot");

            if (b >= 100 && b <= 103)
                b -= 64;
            else if (b == -106)
                b = 40;

            result.set(b, ItemStack.a(nbt));
        });

        return result;
    }

    public void addInInventory(int slot, ItemStack stack) {
        if (isOnline())
            getNmsPlayer().inventory.setItem(slot, stack);
        else {
            modifyData(data -> {
                byte convertSlot = (byte) (slot <= 35 ? slot : slot <= 39 ? slot + 64 : slot == 40 ? -106 : -1);

                NBTTagList list = data.getList("Inventory", 10);

                for (int i = 0; i < list.size(); ++i) {
                    if (((NBTTagCompound) list.get(i)).getByte("Slot") == convertSlot) {
                        list.remove(i);
                        break;
                    }
                }

                NBTTagCompound nbtStack = stack.save(new NBTTagCompound());
                nbtStack.setByte("Slot", convertSlot);

                list.add(nbtStack);
            });
        }
    }

    public List<ItemStack> getEnderChest() {
        if (isOnline())
            return getNmsPlayer().getEnderChest().getContents();

        List<ItemStack> result = NonNullList.a(27, ItemStack.b);

        getData().getList("EnderItems", 10).stream().map(nbtBase -> (NBTTagCompound) nbtBase)
                .forEach(nbt -> result.set(nbt.getByte("Slot"), ItemStack.a(nbt)));

        return result;
    }

    public void addInEnderChest(int slot, ItemStack stack) {
        if (isOnline())
            getNmsPlayer().getEnderChest().setItem(slot, stack);
        else {
            modifyData(data -> {
                NBTTagList list = data.getList("EnderItems", 10);

                for (int i = 0; i < list.size(); ++i) {
                    if (((NBTTagCompound) list.get(i)).getByte("Slot") == slot) {
                        list.remove(i);
                        break;
                    }
                }

                NBTTagCompound nbtStack = stack.save(new NBTTagCompound());
                nbtStack.setByte("Slot", (byte) slot);

                list.add(nbtStack);
            });
        }
    }

    public float getExperience() {
        return isOnline() ? getPlayer().getLevel() + getPlayer().getExp() : getData().getInt("XpLevel") + getData().getFloat("XpP");
    }

    public void setExperience(float experience) {
        if (isOnline()) {
            getPlayer().setLevel(NumberConversions.floor(experience));
            getPlayer().setExp(experience % 1);
        } else {
            modifyData(data -> {
                data.setInt("XpLevel", NumberConversions.floor(experience));
                data.setFloat("XpP", experience % 1);
            });
        }
    }

    public int getFoodLevel() {
        return isOnline() ? getPlayer().getFoodLevel() : getData().getInt("foodLevel");
    }

    public void setFoodLevel(int food) {
        if (isOnline())
            getPlayer().setFoodLevel(food);
        else
            modifyData(data -> data.setInt("foodLevel", food));
    }

    public float getFoodSaturation() {
        return isOnline() ? getPlayer().getSaturation() : getData().getFloat("foodSaturationLevel");
    }

    public void setFoodSaturation(float saturation) {
        if (isOnline())
            getPlayer().setSaturation(saturation);
        else
            modifyData(data -> data.setFloat("foodSaturationLevel", saturation));
    }

    private NBTTagCompound getData() {
        return ((CraftServer) Bukkit.getServer()).getHandle().playerFileData.getPlayerData(getUniqueId().toString());
    }

    private void saveData(NBTTagCompound data) {
        if (isOnline())
            getNmsPlayer().saveData(data);
        else {
            File playerDir = ((CraftServer) Bukkit.getServer()).getHandle().playerFileData.getPlayerDir();

            try {
                File file = File.createTempFile(getUniqueId() + "-", ".dat", playerDir);
                NBTCompressedStreamTools.a(data, file);
                File file1 = new File(playerDir, getUniqueId() + ".dat");
                File file2 = new File(playerDir, getUniqueId() + ".dat_old");
                SystemUtils.a(file1, file, file2);
            } catch (Exception exception) {
                System.err.println("Failed to save player data for " + getUniqueId());
                exception.printStackTrace();
            }
        }
    }

    private void modifyData(Consumer<NBTTagCompound> consumer) {
        NBTTagCompound data = getData();
        consumer.accept(data);
        saveData(data);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AnimalTamer ? getUniqueId() == ((AnimalTamer) obj).getUniqueId() : getOfflinePlayer().equals(obj);
    }
}