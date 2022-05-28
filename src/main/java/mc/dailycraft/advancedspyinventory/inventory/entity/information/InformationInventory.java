package mc.dailycraft.advancedspyinventory.inventory.entity.information;

import mc.dailycraft.advancedspyinventory.inventory.AbstractInventory;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.NonNullList;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public abstract class InformationInventory<T extends LivingEntity> extends AbstractInventory {
    protected final T entity;
    protected final CustomInventoryView oldView;
    protected final List<ItemStack> contents;

    public InformationInventory(Player viewer, T entity, CustomInventoryView oldView, int rows) {
        super(viewer, rows);
        this.entity = entity;
        this.oldView = oldView;
        contents = NonNullList.a(getSize(), ItemStack.b);

        contents.set(getSize() - 9, new ItemStackBuilder("MHF_ArrowLeft", translation.format("interface.information.back")).nms());

        for (int i = getSize() - 8; i < getSize(); ++i)
            contents.set(i, VOID_ITEM);
    }

    @Override
    public List<ItemStack> getContents() {
        return contents;
    }

    @Override
    public ItemStack getItem(int index) {
        return contents.get(index);
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot == getSize() - 9)
            oldView.open();
    }

    @Override
    public void setItem(int index, ItemStack stack) {
    }
}