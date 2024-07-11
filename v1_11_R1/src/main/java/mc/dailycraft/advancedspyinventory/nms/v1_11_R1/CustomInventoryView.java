package mc.dailycraft.advancedspyinventory.nms.v1_11_R1;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class CustomInventoryView extends InventoryView {
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

    @Override
    public Inventory getTopInventory() {
        return inventory;
    }

    @Override
    public Inventory getBottomInventory() {
        return viewer.getInventory();
    }

    @Override
    public HumanEntity getPlayer() {
        return viewer;
    }

    @Override
    public InventoryType getType() {
        return inventory.getType();
    }
}
