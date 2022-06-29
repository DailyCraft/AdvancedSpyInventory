package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.utils.InformationItems;
import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
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
                return new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE, translation.format("interface.donkey.no_chest")).get();
        } else if (index > 1 && index <= 6 || index > 10 && index <= 15 || index > 19 && index <= 24)
            return new ItemStackBuilder(Material.WHITE_STAINED_GLASS_PANE, "").get();
        else if (index == 30)
            return getNonNull(entity.getInventory().getSaddle(), InformationItems.SADDLE.warning(translation));
        else if (index == 32)
            return getNonNull(entity.getInventory().getItem(1), InformationItems.LLAMA_DECOR.get(translation));
        else if (index == getSize() - 3) {
            if (viewer.hasPermission(Permissions.ENTITY_INFORMATION.get(EntityType.LLAMA))) {
                return new ItemStackBuilder(entity.getColor() == Llama.Color.CREAMY ? Material.MILK_BUCKET : entity.getColor() == Llama.Color.GRAY ? Material.DIORITE : Material.valueOf(entity.getColor().name() + "_WOOL"), translation.format("interface.llama.color"))
                        .lore((entity.getColor() == Llama.Color.CREAMY ? "\u25ba " : "  ") + translation.format("interface.llama.creamy"))
                        .lore((entity.getColor() == Llama.Color.WHITE ? "\u25ba " : "  ") + translation.format("interface.llama.white"))
                        .lore((entity.getColor() == Llama.Color.BROWN ? "\u25ba " : "  ") + translation.format("interface.llama.brown"))
                        .lore((entity.getColor() == Llama.Color.GRAY ? "\u25ba " : "  ") + translation.format("interface.llama.gray"))
                        .switchLore(viewer, EntityType.LLAMA).get();
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
                entity.getInventory().setItem(1, stack);
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
            if (viewer.hasPermission(Permissions.ENTITY_INFORMATION_MODIFY.get(EntityType.LLAMA)))
                entity.setColor(Llama.Color.values()[entity.getColor().ordinal() + 1 == Llama.Color.values().length ? 0 : entity.getColor().ordinal() + 1]);
        } else
            super.onClick(event, rawSlot);
    }
}