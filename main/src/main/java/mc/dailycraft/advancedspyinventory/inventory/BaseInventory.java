package mc.dailycraft.advancedspyinventory.inventory;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import mc.dailycraft.advancedspyinventory.utils.Translation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

public abstract class BaseInventory {
    protected static final ItemStack VOID_ITEM = new ItemStackBuilder(Material.GRAY_STAINED_GLASS_PANE, "").get();

    protected final Player viewer;
    protected final Translation translation;
    private final int size;

    public BaseInventory(Player viewer, int rows) {
        this.viewer = viewer;
        translation = Translation.of(viewer);
        size = rows * 9;
    }

    public abstract String getTitle();

    public int getSize() {
        return size;
    }

    public abstract ItemStack getItem(int index);

    public abstract void setItem(int index, ItemStack stack);

    public void onOpen(Player player) {
    }

    public void onClose(Player player) {
    }

    public abstract void onClick(InventoryClickEvent event, int rawSlot);

    public CustomInventoryView getView() {
        return new CustomInventoryView(viewer, this);
    }

    public static ItemStack getNonNull(ItemStack privileged, ItemStack replacer) {
        return privileged != null && privileged.getType() != Material.AIR ? privileged : replacer;
    }

    protected void shift(InventoryClickEvent event, int slot, ItemStack informationItem, Predicate<Material> condition) {
        if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) && event.getCurrentItem() != null && condition.test(event.getCurrentItem().getType()) && event.getInventory().getItem(slot) != null && event.getInventory().getItem(slot).equals(informationItem)) {
            event.setCancelled(true);
            event.getInventory().setItem(slot, event.getCurrentItem());
            event.setCurrentItem(null);

            viewer.updateInventory();
        }
    }

    protected ItemStack getLocationItemStack(Location location, boolean isPlayer) {
        String entityKey = "interface.entity.";

        return new ItemStackBuilder(Material.ARROW, translation.format(entityKey + "location"))
                .lore(translation.format(entityKey + "world", Main.NMS.worldId(location.getWorld()), translation.format(entityKey + "world.environment." + location.getWorld().getEnvironment().name().toLowerCase())))
                .lore(translation.format(entityKey + "x", location.getX()))
                .lore(translation.format(entityKey + "y", location.getY()))
                .lore(translation.format(entityKey + "z", location.getZ()))
                .lore(translation.format(entityKey + "yaw", location.getYaw()))
                .lore(translation.format(entityKey + "pitch", location.getPitch()))
                .lore(isPlayer ? Permissions.PLAYER_TELEPORT.has(viewer) : Permissions.ENTITY_TELEPORT.has(viewer), "", translation.format(entityKey + "teleport")).get();
    }

    protected void replaceItem(InventoryClickEvent event, ItemStack informationItem) {
        if (event.getCurrentItem() != null && event.getCurrentItem().equals(informationItem) && (event.getCursor() == null || event.getCursor().getType() == Material.AIR)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(false);

        if (event.getCurrentItem() != null && event.getCurrentItem().equals(informationItem))
            event.setCurrentItem(null);

        if (event.getCursor().equals(informationItem))
            event.getView().setCursor(null);

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            if (event.getCurrentItem() != null && event.getCurrentItem().equals(informationItem))
                event.setCurrentItem(null);

            if (event.getCursor().equals(informationItem))
                event.getView().setCursor(null);

            viewer.updateInventory();
        });
    }
}