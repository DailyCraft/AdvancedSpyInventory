package mc.dailycraft.advancedspyinventory.utils;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.nms.NMSContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public class CustomInventoryView extends InventoryView {
    private final Player viewer;
    private final NMSContainer container;
    private final Inventory inventory;

    public CustomInventoryView(Player viewer, NMSContainer container) {
        this.viewer = viewer;
        this.container = container;
        inventory = Main.NMS.containerToInventory(container);
    }

    public NMSContainer getContainer() {
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

    @NotNull
    @Override
    public String getTitle() {
        return container.getTitle();
    }

    public void open() {
        viewer.openInventory(this);
    }
}