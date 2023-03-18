package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import org.bukkit.Material;
import org.bukkit.block.Beehive;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BeeInventory extends EntityInventory<Bee> {
    public BeeInventory(Player viewer, Bee entity) {
        super(viewer, entity);
    }

    @Override
    public ItemStack getItem(int index) {
        if (Permissions.hasPermission(EntityType.BEE, viewer)) {
            if (index == getSize() - 3) {
                if (entity.getHive() != null && (entity.getHive().getBlock().getType() == Material.BEEHIVE || entity.getHive().getBlock().getType() == Material.BEE_NEST)) {
                    Beehive hive = (Beehive) entity.getHive().getBlock().getState();
                    org.bukkit.block.data.type.Beehive data = (org.bukkit.block.data.type.Beehive) hive.getBlockData();

                    return new ItemStackBuilder(Material.BEEHIVE, translation.format("interface.bee.hive"))
                            .lore(translation.format("interface.bee.hive.location", hive.getX(), hive.getY(), hive.getZ()))
                            .lore(translation.format("interface.bee.hive.honey", data.getHoneyLevel(), data.getMaximumHoneyLevel()))
                            .lore(translation.format("interface.bee.hive.number", hive.getEntityCount(), hive.getMaxEntities()))
                            .get();
                } else {
                    return new ItemStackBuilder(Material.BEE_NEST, translation.format("interface.bee.hive.no")).get();
                }
            } else if (index == getSize() - 2) {
                return new ItemStackBuilder(Material.HONEYCOMB, formatToggleYesNo(entity.hasNectar(), "interface.bee.nectar")).get();
            } else if (index == getSize() - 1) {
                return new ItemStackBuilder(Material.ARROW, formatToggleYesNo(!entity.hasStung(), "interface.bee.stinger")).get();
            }
        }

        return super.getItem(index);
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (Permissions.hasPermissionModify(EntityType.BEE, viewer)) {
            if (rawSlot == getSize() - 2)
                entity.setHasNectar(!entity.hasNectar());
            else if (rawSlot == getSize() - 1)
                entity.setHasStung(!entity.hasStung());
            else
                super.onClick(event, rawSlot);
        } else
            super.onClick(event, rawSlot);
    }
}