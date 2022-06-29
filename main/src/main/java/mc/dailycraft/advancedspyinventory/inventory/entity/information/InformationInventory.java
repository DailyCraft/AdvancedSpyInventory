package mc.dailycraft.advancedspyinventory.inventory.entity.information;

import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class InformationInventory<T extends LivingEntity> extends BaseInventory {
    protected final T entity;
    protected final CustomInventoryView oldView;
    protected final ItemStack[] contents = new ItemStack[getSize()];

    public InformationInventory(Player viewer, T entity, CustomInventoryView oldView, int rows) {
        super(viewer, rows);
        this.entity = entity;
        this.oldView = oldView;

        for (int i = 0; i < getSize(); ++i)
            contents[i] = new ItemStack(Material.AIR);

        contents[getSize() - 9] = new ItemStackBuilder("MHF_ArrowLeft", translation.format("interface.information.back")).get();

        for (int i = getSize() - 8; i < getSize(); ++i)
            contents[i] = VOID_ITEM;
    }

    @Override
    public ItemStack getItem(int index) {
        return contents[index];
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        contents[index] = stack;
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot == getSize() - 9)
            oldView.open();
    }
}