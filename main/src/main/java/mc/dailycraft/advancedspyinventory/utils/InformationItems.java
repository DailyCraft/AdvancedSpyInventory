package mc.dailycraft.advancedspyinventory.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum InformationItems {
    MAIN_HAND, OFF_HAND, BOOTS, LEGGINGS, CHESTPLATE, HELMET,
    SADDLE, HORSE_ARMOR, LLAMA_DECOR, ENDERMAN_CARRIED, CURSOR;

    public ItemStack get(Translation translation) {
        return new ItemStackBuilder(Material.YELLOW_STAINED_GLASS_PANE, translation.format("item." + name().toLowerCase())).lore(translation.format("item.description"), true).get();
    }

    public ItemStack warning(Translation translation) {
        return new ItemStackBuilder(get(translation)).lore("").lore(translation.format("item.warning"), true).get();
    }

    public ItemStack unavailable(Translation translation) {
        return new ItemStackBuilder(Material.RED_STAINED_GLASS_PANE, translation.format("item." + name().toLowerCase())).lore(translation.format("item.unavailable")).get();
    }
}