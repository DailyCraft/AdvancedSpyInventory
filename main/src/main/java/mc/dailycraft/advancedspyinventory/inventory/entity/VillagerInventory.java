package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.inventory.entity.information.VillagerSpecificationsInventory;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class VillagerInventory extends EntityInventory<Villager> {
    private int tick = 0;

    public VillagerInventory(Player viewer, Villager entity) {
        super(viewer, entity, 4);
    }

    @Override
    public ItemStack getItem(int index) {
        if (tick >= 80)
            tick = 0;

        if (index >= 2 && index <= 6)
            return entity.getInventory().getItem(index - 2);
        else if (index >= 12 && index <= 14)
            return entity.getInventory().getItem(index - 7);
        else if (index == getSize() - 3) {
            if (Permissions.hasPermission(EntityType.VILLAGER, viewer))
                return new ItemStackBuilder(++tick < 40 ? Main.NMS.getVillagerProfessionMaterial(entity.getProfession()) : VillagerSpecificationsInventory.getMaterialOfType(entity.getVillagerType()), translation.format("interface.villager.specifications"))
                        .lore(translation.format("interface.villager.profession", (tick < 40 ? "§l" : "") + translation.format("interface.villager.profession." + entity.getProfession().name().toLowerCase())))
                        .lore(translation.format("interface.villager.type", (tick >= 40 ? "§l" : "") + translation.format("interface.villager.type." + entity.getVillagerType().name().toLowerCase()))).get();
        }

        return super.getItem(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index >= 2 && index <= 6)
            entity.getInventory().setItem(index - 2, stack);
        else if (index >= 12 && index <= 14)
            entity.getInventory().setItem(index - 7, stack);
        else
            super.setItem(index, stack);
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot >= 2 && rawSlot <= 6 || rawSlot >= 12 && rawSlot <= 14) {
            if (Permissions.ENTITY_MODIFY.has(viewer))
                event.setCancelled(false);
        } else if (rawSlot == getSize() - 3) {
            if (Permissions.hasPermissionModify(EntityType.VILLAGER, viewer))
                new VillagerSpecificationsInventory(viewer, entity, (CustomInventoryView) event.getView()).getView().open();
        } else
            super.onClick(event, rawSlot);
    }
}