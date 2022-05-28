package mc.dailycraft.advancedspyinventory.utils;

import net.minecraft.server.v1_16_R3.ItemStack;
import org.bukkit.Material;

public enum InformationItems {
    MAIN_HAND, OFF_HAND, BOOTS, LEGGINGS, CHESTPLATE, HELMET,
    SADDLE, HORSE_ARMOR, LLAMA_DECOR, ENDERMAN_CARRIED, CURSOR;

    public ItemStackBuilder get(Translation translation) {
        return new ItemStackBuilder(Material.YELLOW_STAINED_GLASS_PANE, translation.format("item." + name().toLowerCase())).lore(translation.format("item.description"), true);
    }

    public ItemStackBuilder warning(Translation translation) {
        return get(translation).clone().lore("").lore(translation.format("item.warning"), true);
    }

    public ItemStack unavailable(Translation translation) {
        return new ItemStackBuilder(Material.RED_STAINED_GLASS_PANE, translation.format("item." + name().toLowerCase())).lore(translation.format("item.unavailable")).nms();
    }
}