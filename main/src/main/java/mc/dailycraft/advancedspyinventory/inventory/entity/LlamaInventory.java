package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.utils.InformationItems;
import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class LlamaInventory extends HorseInventory<Llama> {
    private final int strength = entity.getStrength();

    public LlamaInventory(Player viewer, Llama entity) {
        super(viewer, entity, 6);
    }

    @Override
    public ItemStack getItem(int index) {
        if (index > 1 && index <= 1 + strength || index > 10 && index <= 10 + strength || index > 19 && index <= 19 + strength) {
            if (entity.isCarryingChest())
                return entity.getInventory().getItem(index + (index <= 1 + strength ? 0 : index <= 10 + strength ? strength - 9 : strength * 2 - 18));
            else
                return ItemStackBuilder.ofStainedGlassPane(DyeColor.BLACK, translation.format("interface.donkey.no_chest")).get();
        } else if (index > 1 && index <= 6 || index > 10 && index <= 15 || index > 19 && index <= 24)
            return ItemStackBuilder.ofStainedGlassPane(DyeColor.WHITE, "").get();
        else if (index == 30)
            return getNonNull(entity.getInventory().getItem(0), InformationItems.SADDLE.warning(translation));
        else if (index == 32)
            return getNonNull(entity.getInventory().getDecor(), InformationItems.LLAMA_DECOR.get(translation));
        else if (index == getSize() - 3) {
            if (Permissions.hasPermission(EntityType.LLAMA, viewer)) {
                ItemStack stack;

                if (entity.getColor() == Llama.Color.CREAMY)
                    stack = new ItemStack(Material.MILK_BUCKET);
                else if (entity.getColor() == Llama.Color.GRAY)
                    stack = new ItemStack(Material.DIORITE);
                else
                    stack = new ItemStack(Material.getMaterial(entity.getColor().name() + "_WOOL"));

                return new ItemStackBuilder(stack, formatModify("generic.color"))
                        .lore((entity.getColor() == Llama.Color.CREAMY ? "§2\u25ba " : "  ") + translation.format("generic.color.creamy"))
                        .lore((entity.getColor() == Llama.Color.WHITE ? "§2\u25ba " : "  ") + translation.formatColor(DyeColor.WHITE))
                        .lore((entity.getColor() == Llama.Color.BROWN ? "§2\u25ba " : "  ") + translation.formatColor(DyeColor.BROWN))
                        .lore((entity.getColor() == Llama.Color.GRAY ? "§2\u25ba " : "  ") + translation.formatColor(DyeColor.GRAY))
                        .get();
            }
        }

        return super.getItem(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (entity.isCarryingChest() && (index > 1 && index <= 1 + strength || index > 10 && index <= 10 + strength || index > 19 && index <= 19 + strength))
            entity.getInventory().setItem(index + (index <= 1 + strength ? 0 : index <= 10 + strength ? strength - 9 : strength * 2 - 18), stack);

        if (index == 30) {
            if (!stack.equals(InformationItems.SADDLE.warning(translation)))
                entity.getInventory().setItem(0, stack);
        } else if (index == 32) {
            if (!stack.equals(InformationItems.LLAMA_DECOR.get(translation)))
                entity.getInventory().setDecor(stack);
        } else
            super.setItem(index, stack);
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot >= getSize() && Permissions.ENTITY_MODIFY.has(viewer)) {
            shift(event, 30, InformationItems.SADDLE.warning(translation), current -> current == Material.SADDLE);
            shift(event, 32, InformationItems.LLAMA_DECOR.get(translation), current -> current.getKey().getKey().endsWith("_carpet"));
        }

        if (entity.isCarryingChest()) {
            if (Permissions.ENTITY_MODIFY.has(viewer) && (rawSlot > 1 && rawSlot <= 1 + strength || rawSlot > 10 && rawSlot <= 10 + strength || rawSlot > 19 && rawSlot <= 19 + strength))
                event.setCancelled(false);
        }

        if (rawSlot == 30) {
            if (Permissions.ENTITY_MODIFY.has(viewer))
                replaceItem(event, InformationItems.SADDLE.warning(translation));
        } else if (rawSlot == 32) {
            if (Permissions.ENTITY_MODIFY.has(viewer))
                replaceItem(event, InformationItems.LLAMA_DECOR.get(translation));
        } else if (rawSlot == getSize() - 3) {
            if (Permissions.hasPermissionModify(EntityType.LLAMA, viewer))
                ItemStackBuilder.enumLoreClick(event, Llama.Color.values(), entity.getColor(), entity::setColor);
        } else
            super.onClick(event, rawSlot);
    }
}