package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Allay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AllayInventory extends EntityInventory<Allay> {
    public AllayInventory(Player viewer, Allay entity) {
        super(viewer, entity, 3);
    }

    @Override
    public ItemStack getItem(int index) {
        if (index == 4)
            return entity.getInventory().getItem(0);
        else if (index == getSize() - 3) {
            if (viewer.hasPermission(Permissions.ENTITY_INFORMATION.get(EntityType.ALLAY))) {
                UUID memory = entity.getMemory(MemoryKey.LIKED_PLAYER);

                if (memory != null) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(memory);
                    return new ItemStackBuilder(player.getName(), translation.format("interface.allay.owner", player.getName())).get();
                } else
                    return new ItemStackBuilder("MHF_Question", translation.format("interface.allay.unowned")).get();
            }
        }

        return super.getItem(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index == 4)
            entity.getInventory().setItem(0, stack);
        else
            super.setItem(index, stack);
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot == 4) {
            if (Permissions.ENTITY_MODIFY.has(viewer))
                event.setCancelled(false);
        } else
            super.onClick(event, rawSlot);
    }
}