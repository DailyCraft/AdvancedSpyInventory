package mc.dailycraft.advancedspyinventory.utils;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.nms.NMSData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class PlayerData implements AnimalTamer {
    private final UUID playerUuid;
    private final NMSData nms;

    public PlayerData(UUID playerUuid) {
        this.playerUuid = playerUuid;
        nms = Main.NMS.getData(playerUuid);
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

    public boolean isOnline() {
        return getOfflinePlayer().isOnline();
    }

    @Override
    public String getName() {
        return getOfflinePlayer().getName();
    }

    public float getHealth() {
        return isOnline() ? (float) getPlayer().getHealth() : nms.getFloat("Health");
    }

    public void setHealth(float health) {
        if (isOnline())
            getPlayer().setHealth(health);
        else
            nms.putFloat("Health", health);
    }

    public float getMaxHealth() {
        if (isOnline())
            return (float) getPlayer().getMaxHealth();

        return nms.getMaxHealth();
    }

    public void setMaxHealth(float maxHealth) {
        if (isOnline())
            getPlayer().setMaxHealth(maxHealth);
        else
            nms.setMaxHealth(maxHealth);
    }

    public Location getLocation() {
        if (isOnline())
            return getPlayer().getLocation();

        List<Double> position = nms.getDoubleList("Pos");
        List<Float> rotation = nms.getFloatList("Rotation");

        return new Location(
                Bukkit.getWorlds().stream().filter(world -> Main.NMS.worldId(world).equals(nms.getString("Dimension"))).findFirst().orElseGet(() -> Bukkit.getWorlds().get(0)),
                position.get(0), position.get(1), position.get(2), rotation.get(0), rotation.get(1));
    }

    public int getSelectedSlot() {
        return isOnline() ? getPlayer().getInventory().getHeldItemSlot() : nms.getInt("SelectedItemSlot");
    }

    public void setSelectedSlot(int slot) {
        if (isOnline())
            getPlayer().getInventory().setHeldItemSlot(slot);
        else
            nms.putInt("SelectedItemSlot", slot);
    }

    public ItemStack[] getInventory() {
        if (isOnline())
            return getPlayer().getInventory().getContents();

        return nms.getArray("Inventory", 41, i -> {
            if (i >= 100 && i <= 103)
                i -= 64;
            else if (i == -106)
                i = 40;

            return i;
        });
    }

    public void addInInventory(int slot, ItemStack stack) {
        if (isOnline())
            getPlayer().getInventory().setItem(slot, stack);
        else
            nms.setInArray("Inventory", slot <= 35 ? slot : slot <= 39 ? slot + 64 : slot == 40 ? -106 : -1, stack);
    }

    public ItemStack[] getEnderChest() {
        return isOnline() ? getPlayer().getEnderChest().getContents() : nms.getArray("EnderItems", 27, i -> i);
    }

    public void addInEnderChest(int slot, ItemStack stack) {
        if (isOnline())
            getPlayer().getEnderChest().setItem(slot, stack);
        else
            nms.setInArray("EnderItems", slot, stack);
    }

    public float getExperience() {
        return isOnline() ? getPlayer().getLevel() + getPlayer().getExp() : nms.getInt("XpLevel") + nms.getFloat("XpP");
    }

    public void setExperience(float experience) {
        if (isOnline()) {
            getPlayer().setLevel(NumberConversions.floor(experience));
            getPlayer().setExp(experience % 1);
        } else {
            nms.putInt("XpLevel", NumberConversions.floor(experience));
            nms.putFloat("XpP", experience % 1);
        }
    }

    public int getFoodLevel() {
        return isOnline() ? getPlayer().getFoodLevel() : nms.getInt("foodLevel");
    }

    public void setFoodLevel(int food) {
        if (isOnline())
            getPlayer().setFoodLevel(food);
        else
            nms.putInt("foodLevel", food);
    }

    public float getFoodSaturation() {
        return isOnline() ? getPlayer().getSaturation() : nms.getFloat("foodSaturationLevel");
    }

    public void setFoodSaturation(float saturation) {
        if (isOnline())
            getPlayer().setSaturation(saturation);
        else
            nms.putFloat("foodSaturationLevel", saturation);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AnimalTamer ? getUniqueId() == ((AnimalTamer) obj).getUniqueId() : getOfflinePlayer().equals(obj);
    }
}