package mc.dailycraft.advancedspyinventory.inventory;

import mc.dailycraft.advancedspyinventory.utils.*;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.UUID;

public class PlayerInventory extends AbstractInventory {
    private final PlayerData target;

    public PlayerInventory(Player viewer, PlayerData target) {
        super(viewer, 6);
        this.target = target;
    }

    public PlayerInventory(Player viewer, UUID targetUuid) {
        this(viewer, new PlayerData(targetUuid));
    }

    @Override
    public List<ItemStack> getContents() {
        return target.getInventory();
    }

    @Override
    public ItemStack getItem(int index) {
        if (index <= 26)
            return getContents().get(index + 9);
        else if (index <= 35)
            return getContents().get(index - 27);
        else if (index >= 37 && index <= 40)
            return getNonNull(getContents().get(-index + 76), InformationItems.values()[-index + 42].get(translation).nms());

        switch (index) {
            case 42:
                return new ItemStackBuilder(getNonNull(getContents().get(target.getSelectedSlot()).cloneItemStack(), InformationItems.MAIN_HAND.get(translation).nms()))
                        .lore("")
                        .lore(translation.format("interface.player.slot", target.getSelectedSlot() + 1) + (Permissions.PLAYER_SLOT.has(viewer) ? " " + translation.format("interface.player.slot.left") + " " + translation.format("interface.player.slot.right") : ""))
                        .lore(target.isOnline() && Permissions.PLAYER_DROP.has(viewer), translation.format("interface.player.slot.drop"), translation.format("interface.player.slot.drop.all"))
                        .nms();

            case 43:
                return getNonNull(getContents().get(40), InformationItems.OFF_HAND.get(translation).nms());

            case 44:
                if (target.isOnline() && target.getPlayer().getGameMode() != GameMode.CREATIVE)
                    return getNonNull(target.getNmsPlayer().inventory.getCarried(), InformationItems.CURSOR.get(translation).nms());
                else
                    return InformationItems.CURSOR.unavailable(translation);

            case 46:
                if (Permissions.PLAYER_HEALTH.has(viewer)) {
                    return new ItemStackBuilder(PotionType.INSTANT_HEAL, translation.format("interface.entity.health", target.getHealth(), target.getMaxHealth()))
                            .lore(Permissions.PLAYER_HEALTH_MODIFY.has(viewer), translation.format("interface.entity.health.modify"))
                            .lore(Permissions.PLAYER_HEALTH_MODIFY_MAX.has(viewer), translation.format("interface.entity.health.modify.max")).nms();
                }

                break;

            case 47:
                if (Permissions.PLAYER_LOCATION.has(viewer))
                    return getLocationItemStack(target.getLocation(), true);

                break;

            case 49:
                return new ItemStackBuilder(Material.BARRIER, Permissions.PLAYER_MODIFY.has(viewer) ? translation.format("interface.entity.clear") : translation.format("interface.entity.close")).nms();

            case 51:
                if (Permissions.PLAYER_XP.has(viewer)) {
                    return new ItemStackBuilder(Material.EXPERIENCE_BOTTLE, translation.format("interface.player.experience", target.getExperience()))
                            .lore(translation.format("interface.player.experience.modify"), Permissions.PLAYER_XP_MODIFY.has(viewer)).nms();
                }

                break;

            case 52:
                if (Permissions.PLAYER_FOOD.has(viewer)) {
                    return new ItemStackBuilder(Material.COOKED_BEEF, translation.format("interface.player.food.level", target.getFoodLevel()))
                            .lore(translation.format("interface.player.food.saturation", target.getFoodSaturation()))
                            .lore(Permissions.PLAYER_FOOD_MODIFY.has(viewer), "", translation.format("interface.player.food.level.modify"), translation.format("interface.player.food.saturation.modify"))
                            .nms();
                }

                break;

            case 53:
                if (Permissions.ENDER.has(viewer))
                    return new ItemStackBuilder(Material.ENDER_CHEST, translation.format("interface.player.enderchest")).nms();

                break;
        }

        return VOID_ITEM;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index <= 26)
            set(index + 9, stack);
        else if (index <= 35)
            set(index - 27, stack);
        else if (index >= 37 && index <= 40 && !stack.equals(InformationItems.values()[-index + 42].get(translation).nms()))
            set(-index + 76, stack);
        else if (index == 43 && !stack.equals(InformationItems.OFF_HAND.get(translation).nms()))
            set(40, stack);
        else if (index == 44 && !stack.equals(InformationItems.CURSOR.get(translation).nms()) && target.isOnline() && target.getPlayer().getGameMode() != GameMode.CREATIVE) {
            target.getNmsPlayer().inventory.setCarried(stack);
            target.getNmsPlayer().broadcastCarriedItem();
        }

        ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(new PacketPlayOutSetSlot(((CraftPlayer) viewer).getHandle().activeContainer.windowId, 42, getItem(42)));
    }

    private void set(int index, ItemStack stack) {
        getContents().set(index, stack);
        target.addInInventory(index, stack);
    }

    @Override
    public String getTitle() {
        return translation.format("interface.entity.title", target.getName());
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot <= 35) {
            if (target.equals(viewer)) {
                viewer.sendMessage(translation.format("interface.player.own"));
            } else {
                if (Permissions.PLAYER_MODIFY.has(viewer)) {
                    event.setCancelled(false);
                    event.getInventory().setItem(42, CraftItemStack.asBukkitCopy(getItem(42)));
                } else
                    viewer.sendMessage(translation.format("interface.entity.modify"));
            }
        } else if (rawSlot >= 37 && rawSlot <= 40) {
            if (!target.equals(viewer)) {
                if (Permissions.PLAYER_MODIFY.has(viewer))
                    replaceItem(event, InformationItems.values()[-rawSlot + 42].get(translation).get());
                else
                    viewer.sendMessage(translation.format("interface.entity.modify"));
            } else
                viewer.sendMessage(translation.format("interface.player.own"));
        } else if (rawSlot == 42) {
            if (Permissions.PLAYER_SLOT.has(viewer)) {
                if (event.getClick() == ClickType.LEFT) {
                    if (target.getSelectedSlot() > 0)
                        target.setSelectedSlot(target.getSelectedSlot() - 1);
                    else
                        target.setSelectedSlot(8);
                } else if (event.getClick() == ClickType.RIGHT) {
                    if (target.getSelectedSlot() < 8)
                        target.setSelectedSlot(target.getSelectedSlot() + 1);
                    else
                        target.setSelectedSlot(0);
                }
            }

            if (target.isOnline() && Permissions.PLAYER_DROP.has(viewer)) {
                if (event.getClick() == ClickType.SHIFT_LEFT)
                    target.getPlayer().dropItem(false);
                else if (event.getClick() == ClickType.SHIFT_RIGHT)
                    target.getPlayer().dropItem(true);
            }
        } else if (rawSlot == 43) {
            if (!target.equals(viewer)) {
                if (Permissions.PLAYER_MODIFY.has(viewer))
                    replaceItem(event, InformationItems.OFF_HAND.get(translation).get());
                else
                    viewer.sendMessage(translation.format("interface.entity.modify"));
            } else
                viewer.sendMessage(translation.format("interface.player.own"));
        } else if (rawSlot == 44) {
            if (target.isOnline() && target.getPlayer().getGameMode() != GameMode.CREATIVE) {
                if (!target.equals(viewer)) {
                    if (Permissions.PLAYER_MODIFY.has(viewer))
                        replaceItem(event, InformationItems.CURSOR.get(translation).get());
                    else
                        viewer.sendMessage(translation.format("interface.entity.modify"));
                } else
                    viewer.sendMessage(translation.format("interface.player.own"));
            }
        } else if (rawSlot == getSize() - 8) {
            if (Permissions.PLAYER_HEALTH_MODIFY.has(viewer) && event.getClick() == ClickType.LEFT)
                signInterface((CustomInventoryView) event.getView(), "health", target.getHealth(), 0f, target.getMaxHealth(), Float::parseFloat, target::setHealth);
            else if (Permissions.PLAYER_HEALTH_MODIFY_MAX.has(viewer) && event.getClick() == ClickType.RIGHT)
                signInterface((CustomInventoryView) event.getView(), "health.max", target.getMaxHealth(), 0f, Float.MAX_VALUE, Float::parseFloat, target::setMaxHealth);
        } else if (rawSlot == getSize() - 7) {
            if (Permissions.PLAYER_TELEPORT.has(viewer)) {
                viewer.teleport(target.getLocation());
                viewer.closeInventory();
            }
        } else if (rawSlot == getSize() - 5) {
            if (Permissions.PLAYER_MODIFY.has(viewer))
                event.getInventory().clear();
            else
                viewer.closeInventory();
        } else if (rawSlot == getSize() - 3) {
            if (Permissions.PLAYER_XP_MODIFY.has(viewer))
                signInterface((CustomInventoryView) event.getView(), "experience", target.getExperience(), 0f, Float.MAX_VALUE, Float::parseFloat, target::setExperience);
        } else if (rawSlot == getSize() - 2) {
            if (Permissions.PLAYER_FOOD_MODIFY.has(viewer) && (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT)) {
                if (event.getClick() == ClickType.LEFT)
                    signInterface((CustomInventoryView) event.getView(), "food.level", target.getFoodLevel(), 0, Integer.MAX_VALUE, Integer::parseInt, target::setFoodLevel);
                else
                    signInterface((CustomInventoryView) event.getView(), "food.saturation", target.getFoodSaturation(), 0f, Float.MAX_VALUE, Float::parseFloat, target::setFoodSaturation);
            }
        } else if (rawSlot == getSize() - 1) {
            if (target.equals(viewer) && Permissions.ENDER.has(viewer) || Permissions.ENDER_OTHERS.has(viewer))
                new EnderChestInventory(viewer, target).getView().open();
        } else if (rawSlot >= getSize()) {
            if (target.equals(viewer))
                viewer.sendMessage(translation.format("interface.player.own"));
            else {
                if (Permissions.PLAYER_MODIFY.has(viewer)) {
                    event.setCancelled(false);

                    shift(event, 37, InformationItems.HELMET.get(translation).get(), current -> current.getItem() instanceof ItemArmor && ((ItemArmor) current.getItem()).b() == EnumItemSlot.HEAD);
                    shift(event, 38, InformationItems.CHESTPLATE.get(translation).get(), current -> current.getItem() instanceof ItemArmor && ((ItemArmor) current.getItem()).b() == EnumItemSlot.CHEST);
                    shift(event, 39, InformationItems.LEGGINGS.get(translation).get(), current -> current.getItem() instanceof ItemArmor && ((ItemArmor) current.getItem()).b() == EnumItemSlot.LEGS);
                    shift(event, 40, InformationItems.BOOTS.get(translation).get(), current -> current.getItem() instanceof ItemArmor && ((ItemArmor) current.getItem()).b() == EnumItemSlot.FEET);
                }
            }
        }
    }
}