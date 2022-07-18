package mc.dailycraft.advancedspyinventory.utils;

import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CustomInventoryViewNew extends CustomInventoryView {
    public CustomInventoryViewNew(Player viewer, BaseInventory container) {
        super(viewer, container);
    }

    @NotNull
    @Override
    public String getTitle() {
        return getContainer().getTitle();
    }
}
