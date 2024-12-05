package mc.dailycraft.advancedspyinventory.inventory;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.utils.InformationItems;
import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import mc.dailycraft.advancedspyinventory.utils.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.UUID;

public class PlayerInventory extends BaseInventory {
    private final PlayerData target;

    public PlayerInventory(Player viewer, PlayerData target) {
        super(viewer, 6);
        this.target = target;
    }

    public PlayerInventory(Player viewer, UUID targetUuid) {
        this(viewer, new PlayerData(targetUuid));
    }

    @Override
    public ItemStack getItem(int index) {
        ItemStack[] inventory = target.getInventory();

        if (index <= 26)
            return inventory[index + 9];
        else if (index <= 35)
            return inventory[index - 27];
        else if (index >= 37 && index <= 40)
            return getNonNull(inventory[-index + 76], InformationItems.values()[-index + 42].get(translation));

        switch (index) {
            case 42:
                return new ItemStackBuilder(getNonNull(inventory[target.getSelectedSlot()] != null ? inventory[target.getSelectedSlot()].clone() : null, InformationItems.MAIN_HAND.get(translation)))
                        .lore("")
                        .lore(translation.format("interface.player.slot", target.getSelectedSlot() + 1) + (Permissions.PLAYER_SLOT.has(viewer) ? " " + translation.format("interface.player.slot.modify") : ""))
                        .lore(target.isOnline() && Permissions.PLAYER_DROP.has(viewer), translation.format("interface.player.slot.drop"), translation.format("interface.player.slot.drop.all"))
                        .get();

            case 43:
                return getNonNull(inventory[40], InformationItems.OFF_HAND.get(translation));

            case 44:
                if (target.isOnline() && target.getPlayer().getGameMode() != GameMode.CREATIVE)
                    return getNonNull(target.getPlayer().getItemOnCursor(), InformationItems.CURSOR.get(translation));
                else
                    return InformationItems.CURSOR.unavailable(translation);

            case 46:
                if (Permissions.PLAYER_HEALTH.has(viewer)) {
                    return new ItemStackBuilder(Main.VERSION >= 20.5 ? PotionType.HEALING : PotionType.valueOf("INSTANT_HEAL"), translation.format("interface.entity.health", target.getHealth(), target.getMaxHealth()))
                            .lore(Permissions.PLAYER_HEALTH_MODIFY.has(viewer) || Permissions.PLAYER_HEALTH_MODIFY.has(viewer), "", translation.format("interface.entity.health.modify.0"))
                            .lore(Permissions.PLAYER_HEALTH_MODIFY.has(viewer), "   " + translation.format("interface.entity.health.modify.1"))
                            .lore(Permissions.PLAYER_HEALTH_MODIFY_MAX.has(viewer), "   " + translation.format("interface.entity.health.modify.2")).get();
                }

                break;

            case 47:
                if (Permissions.PLAYER_LOCATION.has(viewer))
                    return getLocationItemStack(target.getLocation(), true);

                break;

            case 49:
                return new ItemStackBuilder(Material.BARRIER, translation.format("interface.entity.close"))
                        .lore(Permissions.PLAYER_MODIFY.has(viewer), "", translation.format("interface.entity.clear"), translation.format("interface.entity.clear.warning")).get();

            case 51:
                if (Permissions.PLAYER_XP.has(viewer))
                    return new ItemStackBuilder(Main.VERSION >= 13 ? Material.EXPERIENCE_BOTTLE : Material.getMaterial("EXP_BOTTLE"), translation.format("interface.player.experience", target.getExperience()) + (Permissions.PLAYER_XP_MODIFY.has(viewer) ? " " + translation.format("generic.modify") : "")).get();

                break;

            case 52:
                if (Permissions.PLAYER_FOOD.has(viewer)) {
                    return new ItemStackBuilder(Material.COOKED_BEEF, translation.format("interface.player.food.level", target.getFoodLevel()) + (Permissions.PLAYER_FOOD_MODIFY.has(viewer) ? " " + translation.format("generic.modify.left") : ""))
                            .lore(ChatColor.WHITE + translation.format("interface.player.food.saturation", target.getFoodSaturation()) + (Permissions.PLAYER_FOOD_MODIFY.has(viewer) ? " " + translation.format("generic.modify.right") : ""))
                            .lore(ChatColor.WHITE + translation.format("interface.player.food.exhaustion", target.getExhaustion()) + (Permissions.PLAYER_FOOD_MODIFY.has(viewer) ? " " + translation.format("generic.modify.shift") : ""))
                            .get();
                }

                break;

            case 53:
                if (Permissions.ENDER.has(viewer))
                    return new ItemStackBuilder(Material.ENDER_CHEST, translation.format("interface.player.enderchest")).get();
        }

        return VOID_ITEM;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index <= 26)
            target.addInInventory(index + 9, stack);
        else if (index <= 35)
            target.addInInventory(index - 27, stack);
        else if (index >= 37 && index <= 40 && !stack.equals(InformationItems.values()[-index + 42].get(translation)))
            target.addInInventory(-index + 76, stack);
        else if (index == 43 && !stack.equals(InformationItems.OFF_HAND.get(translation)))
            target.addInInventory(40, stack);
        else if (index == 44 && !stack.equals(InformationItems.CURSOR.get(translation)) && target.isOnline() && target.getPlayer().getGameMode() != GameMode.CREATIVE)
            target.getPlayer().setItemOnCursor(stack);
    }

    @Override
    public String getTitle() {
        return translation.format("interface.entity.title", target.getName());
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot <= 35) {
            if (target.equals(viewer))
                viewer.sendMessage(translation.format("interface.player.own"));
            else {
                if (Permissions.PLAYER_MODIFY.has(viewer)) {
                    event.setCancelled(false);
                    event.getInventory().setItem(42, getItem(42));
                } else
                    viewer.sendMessage(translation.format("interface.entity.modify"));
            }
        } else if (rawSlot >= 37 && rawSlot <= 40) {
            if (!target.equals(viewer)) {
                if (Permissions.PLAYER_MODIFY.has(viewer))
                    replaceItem(event, InformationItems.values()[-rawSlot + 42].get(translation));
                else
                    viewer.sendMessage(translation.format("interface.entity.modify"));
            } else
                viewer.sendMessage(translation.format("interface.player.own"));
        } else if (rawSlot == 42) {
            if (Permissions.PLAYER_SLOT.has(viewer)) {
                if (event.getClick() == ClickType.LEFT)
                    target.setSelectedSlot((target.getSelectedSlot() + 8) % 9);
                else if (event.getClick() == ClickType.RIGHT)
                    target.setSelectedSlot((target.getSelectedSlot() + 1) % 9);
                else if (event.getClick() == ClickType.NUMBER_KEY)
                    target.setSelectedSlot(event.getHotbarButton());
            }

            if (target.isOnline() && Permissions.PLAYER_DROP.has(viewer)) {
                if (event.getClick() == ClickType.SHIFT_LEFT)
                    Main.NMS.dropItem(target.getPlayer(), false);
                else if (event.getClick() == ClickType.SHIFT_RIGHT)
                    Main.NMS.dropItem(target.getPlayer(), true);

                target.getPlayer().updateInventory();
            }
        } else if (rawSlot == 43) {
            if (!target.equals(viewer)) {
                if (Permissions.PLAYER_MODIFY.has(viewer))
                    replaceItem(event, InformationItems.OFF_HAND.get(translation));
                else
                    viewer.sendMessage(translation.format("interface.entity.modify"));
            } else
                viewer.sendMessage(translation.format("interface.player.own"));
        } else if (rawSlot == 44) {
            if (target.isOnline() && target.getPlayer().getGameMode() != GameMode.CREATIVE) {
                if (!target.equals(viewer)) {
                    if (Permissions.PLAYER_MODIFY.has(viewer))
                        replaceItem(event, InformationItems.CURSOR.get(translation));
                    else
                        viewer.sendMessage(translation.format("interface.entity.modify"));
                } else
                    viewer.sendMessage(translation.format("interface.player.own"));
            }
        } else if (rawSlot == getSize() - 8) {
            if (Permissions.PLAYER_HEALTH_MODIFY.has(viewer) && event.isLeftClick())
                openSign("health", target.getHealth(), 0f, target.getMaxHealth(), Float::parseFloat, target::setHealth);
            else if (Permissions.PLAYER_HEALTH_MODIFY_MAX.has(viewer) && event.isRightClick())
                openSign("health.max", target.getMaxHealth(), 0.1f, Float.MAX_VALUE, Float::parseFloat, target::setMaxHealth);
        } else if (rawSlot == getSize() - 7) {
            if (Permissions.PLAYER_TELEPORT.has(viewer)) {
                if (event.isShiftClick()) {
                    if (Permissions.PLAYER_TELEPORT_OTHERS.has(viewer) && !target.equals(viewer))
                        target.setLocation(viewer.getLocation());
                } else
                    viewer.teleport(target.getLocation());
            }
        } else if (rawSlot == getSize() - 5) {
            if (Permissions.PLAYER_MODIFY.has(viewer) && event.isShiftClick())
                event.getInventory().clear();
            else
                viewer.closeInventory();
        } else if (rawSlot == getSize() - 3) {
            if (Permissions.PLAYER_XP_MODIFY.has(viewer))
                openSign("experience", target.getExperience(), 0f, Float.MAX_VALUE, Float::parseFloat, target::setExperience);
        } else if (rawSlot == getSize() - 2) {
            if (Permissions.PLAYER_FOOD_MODIFY.has(viewer)) {
                if (event.getClick().isShiftClick())
                    openSign("food.exhaustion", target.getExhaustion(), 0f, Float.MAX_VALUE, Float::parseFloat, target::setExhaustion);
                else if (event.getClick().isLeftClick())
                    openSign("food.level", target.getFoodLevel(), 0, Integer.MAX_VALUE, Integer::parseInt, target::setFoodLevel);
                else if (event.isRightClick())
                    openSign("food.saturation", target.getFoodSaturation(), 0f, Float.MAX_VALUE, Float::parseFloat, target::setFoodSaturation);
            }
        } else if (rawSlot == getSize() - 1) {
            if (target.equals(viewer) && Permissions.ENDER.has(viewer) || Permissions.ENDER_OTHERS.has(viewer))
                new EnderChestInventory(viewer, target).open();
        } else if (rawSlot >= getSize()) {
            if (target.equals(viewer))
                viewer.sendMessage(translation.format("interface.player.own"));
            else {
                if (Permissions.PLAYER_MODIFY.has(viewer)) {
                    event.setCancelled(false);

                    shift(event, 37, EquipmentSlot.HEAD, item -> item::get, "_helmet");
                    shift(event, 38, EquipmentSlot.CHEST, item -> item::get, "_chestplate");
                    shift(event, 39, EquipmentSlot.LEGS, item -> item::get, "_leggings");
                    shift(event, 40, EquipmentSlot.FEET, item -> item::get, "_boots");
                }
            }
        }
    }
}