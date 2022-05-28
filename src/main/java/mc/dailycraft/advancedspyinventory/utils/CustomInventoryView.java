package mc.dailycraft.advancedspyinventory.utils;

import mc.dailycraft.advancedspyinventory.inventory.AbstractInventory;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public class CustomInventoryView extends InventoryView {
    private final Player viewer;
    private final AbstractInventory inventory;
    private final Inventory craftInventory;

    public CustomInventoryView(Player viewer, AbstractInventory inventory) {
        this.viewer = viewer;
        this.inventory = inventory;
        craftInventory = new CraftInventory(inventory);
    }

    public AbstractInventory getInventory() {
        return inventory;
    }

    @NotNull
    @Override
    public Inventory getTopInventory() {
        return craftInventory;
    }

    @NotNull
    @Override
    public Inventory getBottomInventory() {
        return viewer.getInventory();
    }

    @NotNull
    @Override
    public Player getPlayer() {
        return viewer;
    }

    @NotNull
    @Override
    public InventoryType getType() {
        return craftInventory.getType();
    }

    @NotNull
    @Override
    public String getTitle() {
        return inventory.getTitle();
    }

    public void open() {
        viewer.openInventory(this);
    }
}