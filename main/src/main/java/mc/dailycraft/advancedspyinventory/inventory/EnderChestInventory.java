package mc.dailycraft.advancedspyinventory.inventory;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.utils.ClassChange;
import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import mc.dailycraft.advancedspyinventory.utils.PlayerData;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class EnderChestInventory extends BaseInventory {
    private final PlayerData target;

    public EnderChestInventory(Player viewer, PlayerData target) {
        super(viewer, 4);
        this.target = target;
    }

    public EnderChestInventory(Player viewer, UUID targetUuid) {
        this(viewer, new PlayerData(targetUuid));
    }

    @Override
    public ItemStack getItem(int index) {
        if (index < getSize() - 9)
            return target.getEnderChest()[index];
        else if (index == getSize() - 9) {
            if (Permissions.PLAYER_VIEW.has(viewer))
                return new ItemStackBuilder(Material.CHEST, translation.format("interface.enderchest.inventory")).get();
        } else if (index == getSize() - 5)
            return new ItemStackBuilder(Material.BARRIER, translation.format("interface.entity.close"))
                    .lore(target.equals(viewer) && Permissions.ENDER_MODIFY.has(viewer) || Permissions.ENDER_OTHERS_MODIFY.has(viewer), "", translation.format("interface.entity.clear"), translation.format("interface.entity.clear.warning")).get();

        return VOID_ITEM;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index < getSize() - 9)
            target.addInEnderChest(index, stack);
    }

    @Override
    public void onOpen(Player player) {
        viewer.playSound(viewer.getLocation(), ClassChange.enumValueOf(Sound.class, "BLOCK_ENDER" + (Main.VERSION >= 13 ? "_" : "") + "CHEST_OPEN"), 2, 1);
    }

    @Override
    public void onClose(Player player) {
        viewer.playSound(viewer.getLocation(), ClassChange.enumValueOf(Sound.class, "BLOCK_ENDER" + (Main.VERSION >= 13 ? "_" : "") + "CHEST_CLOSE"), 2, 1);
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
            if (Permissions.PLAYER_VIEW.has(viewer))
                new PlayerInventory(viewer, target).open();
        } else if (rawSlot == getSize() - 5) {
            if ((target.equals(viewer) && Permissions.ENDER_MODIFY.has(viewer) || Permissions.ENDER_OTHERS_MODIFY.has(viewer)) && event.isShiftClick())
                event.getInventory().clear();
            else
                viewer.closeInventory();
        }
    }
}