package mc.dailycraft.advancedspyinventory.inventory;

import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import mc.dailycraft.advancedspyinventory.utils.PlayerData;
import net.minecraft.server.v1_16_R3.ItemStack;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.UUID;

public class EnderChestInventory extends AbstractInventory {
    private final PlayerData target;

    public EnderChestInventory(Player viewer, PlayerData target) {
        super(viewer, 4);
        this.target = target;
    }

    public EnderChestInventory(Player viewer, UUID targetUuid) {
        this(viewer, new PlayerData(targetUuid));
    }

    @Override
    public List<ItemStack> getContents() {
        return target.getEnderChest();
    }

    @Override
    public ItemStack getItem(int index) {
        if (index < getSize() - 9)
            return getContents().get(index);
        else if (index == getSize() - 9) {
            if (Permissions.INVENTORY.has(viewer))
                return new ItemStackBuilder(Material.CHEST, translation.format("interface.enderchest.inventory")).nms();
        } else if (index == getSize() - 5)
            return new ItemStackBuilder(Material.BARRIER, translation.format("interface." + (target.equals(viewer) && Permissions.ENDER_MODIFY.has(viewer) || Permissions.ENDER_OTHERS_MODIFY.has(viewer) ? "enderchest.clear" : "entity.close"))).nms();

        return VOID_ITEM;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index < getSize() - 9) {
            getContents().set(index, stack);
            target.addInEnderChest(index, stack);
        }
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        super.onOpen(who);
        viewer.playSound(viewer.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 2, 1);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        super.onClose(who);
        viewer.playSound(viewer.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 2, 1);
    }

    @Override
    public String getTitle() {
        return translation.format("interface.enderchest.title", target.getName());
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot < getSize() - 9 || rawSlot >= getSize()) {
            if (target.equals(viewer) && Permissions.ENDER_MODIFY.has(viewer) || Permissions.ENDER_OTHERS_MODIFY.has(viewer))
                event.setCancelled(false);
            else if (rawSlot < getSize() - 9)
                viewer.sendMessage(translation.format("permission.enderchest.modify"));
        } else if (rawSlot == getSize() - 9) {
            if (Permissions.INVENTORY.has(viewer))
                new PlayerInventory(viewer, target).getView().open();
        } else if (rawSlot == getSize() - 5) {
            if (target.equals(viewer) && Permissions.ENDER_MODIFY.has(viewer) || Permissions.ENDER_OTHERS_MODIFY.has(viewer))
                event.getInventory().clear();
            else
                viewer.closeInventory();
        }
    }
}