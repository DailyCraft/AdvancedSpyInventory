package mc.dailycraft.advancedspyinventory.utils;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public abstract class CustomInventoryView extends InventoryView {
    private final Player viewer;
    private final BaseInventory container;
    private final Inventory inventory;

    public CustomInventoryView(Player viewer, BaseInventory container) {
        this.viewer = viewer;
        inventory = Main.NMS.createInventory(this.container = container);
    }

    public BaseInventory getContainer() {
        return container;
    }

    @NotNull
    @Override
    public Inventory getTopInventory() {
        return inventory;
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
        return inventory.getType();
    }

    public void open() {
        Main.NMS.openInventory(viewer, this);
    }
}