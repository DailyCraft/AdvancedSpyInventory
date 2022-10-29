package mc.dailycraft.advancedspyinventory.utils;

import org.bukkit.DyeColor;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public enum InformationItems {
    MAIN_HAND, OFF_HAND, BOOTS, LEGGINGS, CHESTPLATE, HELMET,
    SADDLE, HORSE_ARMOR, LLAMA_DECOR, ENDERMAN_CARRIED, CURSOR;

    public static InformationItems of(EquipmentSlot slot) {
        switch (slot) {
            case HAND:
                return MAIN_HAND;
            case OFF_HAND:
                return OFF_HAND;
            case FEET:
                return BOOTS;
            case LEGS:
                return LEGGINGS;
            case CHEST:
                return CHESTPLATE;
            case HEAD:
                return HELMET;
            default:
                return null;
        }
    }

    public ItemStack get(Translation translation) {
        return ItemStackBuilder.ofStainedGlassPane(DyeColor.YELLOW, translation.format("item." + name().toLowerCase())).lore(translation.format("item.description"), true).get();
    }

    public ItemStack warning(Translation translation) {
        return new ItemStackBuilder(get(translation)).lore("").lore(translation.format("item.warning"), true).get();
    }

    public ItemStack unavailable(Translation translation) {
        return ItemStackBuilder.ofStainedGlassPane(DyeColor.RED, translation.format("item." + name().toLowerCase())).lore(translation.format("item.unavailable")).get();
    }
}