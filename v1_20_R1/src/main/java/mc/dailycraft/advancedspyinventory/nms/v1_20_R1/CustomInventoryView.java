package mc.dailycraft.advancedspyinventory.nms.v1_20_R1;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

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
    public HumanEntity getPlayer() {
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

    @NotNull
    @Override
    public String getOriginalTitle() {
        return container.getTitle();
    }

    @Override
    public void setTitle(@NotNull String title) {}
}
