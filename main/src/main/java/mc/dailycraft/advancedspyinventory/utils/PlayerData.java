package mc.dailycraft.advancedspyinventory.utils;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.nms.NMSData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerData implements AnimalTamer {
    public static final Attribute MAX_HEALTH_ATTRIBUTE = Main.VERSION >= 21.3 ? Attribute.MAX_HEALTH : Attribute.valueOf("GENERIC_MAX_HEALTH");

    private final UUID playerUuid;
    private final NMSData nms;

    public PlayerData(UUID playerUuid) {
        nms = Main.NMS.getData(this.playerUuid = playerUuid);
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
        return isOnline()
                ? (float) getPlayer().getAttribute(MAX_HEALTH_ATTRIBUTE).getValue()
                : nms.getMaxHealth();
    }

    public void setMaxHealth(float maxHealth) {
        if (isOnline())
            getPlayer().getAttribute(MAX_HEALTH_ATTRIBUTE).setBaseValue(maxHealth);
        else
            nms.setMaxHealth(maxHealth);
    }

    public Location getLocation() {
        if (isOnline())
            return getPlayer().getLocation();

        double[] position = nms.getList("Pos");
        double[] rotation = nms.getList("Rotation");

        return new Location(
                Bukkit.getWorlds().stream()
                        .filter(world -> world.getUID().getLeastSignificantBits() == nms.getLong("WorldUUIDLeast") && world.getUID().getMostSignificantBits() == nms.getLong("WorldUUIDMost"))
                        .findFirst().orElse(Bukkit.getWorlds().get(0)),
                position[0], position[1], position[2], (float) rotation[0], (float) rotation[1]);
    }

    public void setLocation(Location location) {
        if (isOnline())
            getPlayer().teleport(location);
        else {
            nms.putList("Pos", new double[] {location.getX(), location.getY(), location.getZ()}, false);
            nms.putList("Rotation", new double[] {location.getYaw(), location.getPitch()}, true);
            nms.putLong("WorldUUIDLeast", location.getWorld().getUID().getLeastSignificantBits());
            nms.putLong("WorldUUIDMost", location.getWorld().getUID().getMostSignificantBits());

            if (Main.VERSION >= 16)
                nms.putString("Dimension", Main.NMS.worldKey(location.getWorld()).toString());
        }
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

    public float getExhaustion() {
        return isOnline() ? getPlayer().getExhaustion() : nms.getFloat("foodExhaustionLevel");
    }

    public void setExhaustion(float exhaustion) {
        if (isOnline())
            getPlayer().setExhaustion(exhaustion);
        else
            nms.putFloat("foodExhaustionLevel", exhaustion);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AnimalTamer ? getUniqueId() == ((AnimalTamer) obj).getUniqueId() : getOfflinePlayer().equals(obj);
    }
}