package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import net.minecraft.server.v1_16_R3.ItemStack;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftChestedHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class DonkeyInventory extends HorseInventory<ChestedHorse> {
    public DonkeyInventory(Player viewer, ChestedHorse entity) {
        super(viewer, entity, 6);
    }

    @Override
    public ItemStack getItem(int index) {
        if (index > 1 && index <= 6 || index > 10 && index <= 15 || index > 19 && index <= 24) {
            if (entity.isCarryingChest())
                return getContents().get(6 + index - (index <= 6 ? 0 : index <= 15 ? 4 : 8));
            else
                return new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE, translation.format("interface.donkey.no_chest")).nms();
        }

        return super.getItem(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (entity.isCarryingChest()) {
            if (index > 1 && index <= 6 || index > 10 && index <= 15 || index > 19 && index <= 24) {
                int i = 8 + index - (index <= 6 ? 2 : index <= 15 ? 6 : 10);
                getContents().set(i, stack);
                ((CraftChestedHorse) entity).getHandle().inventoryChest.setItem(i - 6, stack);
            }
        }

        super.setItem(index, stack);
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (entity.isCarryingChest()) {
            if (Permissions.ENTITY_MODIFY.has(viewer) && (rawSlot > 1 && rawSlot <= 6 || rawSlot > 10 && rawSlot <= 15 || rawSlot > 19 && rawSlot <= 24))
                event.setCancelled(false);
        }

        super.onClick(event, rawSlot);
    }
}