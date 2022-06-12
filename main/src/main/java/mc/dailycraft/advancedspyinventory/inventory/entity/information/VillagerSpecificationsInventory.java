package mc.dailycraft.advancedspyinventory.inventory.entity.information;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryClickEvent;

public class VillagerSpecificationsInventory extends InformationInventory<Villager> {
    public VillagerSpecificationsInventory(Player viewer, Villager entity, CustomInventoryView oldView) {
        super(viewer, entity, oldView, 6);

        for (int i = 0; i < 36; ++i) {
            contents[i] = VOID_ITEM;

            if (i == 8)
                i = 26;
        }

        contents[4] = new ItemStackBuilder(Material.CRAFTING_TABLE, translation.format("interface.villager.specifications.profession"))
                .lore(translation.format("interface.villager.profession", translation.format("interface.villager.profession." + entity.getProfession().name().toLowerCase()))).get();
        contents[31] = new ItemStackBuilder(Material.OAK_LOG, translation.format("interface.villager.specifications.type"))
                .lore(translation.format("interface.villager.type", translation.format("interface.villager.type." + entity.getVillagerType().name().toLowerCase()))).get();

        for (int i = 0; i < Villager.Profession.values().length; ++i) {
            Villager.Profession profession = Villager.Profession.values()[i];
            contents[i + 9] = new ItemStackBuilder(Main.NMS.getVillagerProfessionMaterial(profession), translation.format("interface.villager.profession." + profession.name().toLowerCase()))
                    .lore(translation.format("interface.information.select" + (entity.getProfession() == profession ? "ed" : "")))
                    .enchant(entity.getProfession() == profession).get();
        }

        for (int i = 0; i < Villager.Type.values().length; ++i) {
            Villager.Type type = Villager.Type.values()[i];
            contents[i + 36] = new ItemStackBuilder(getMaterialOfType(type), translation.format("interface.villager.type." + type.name().toLowerCase()))
                    .lore(translation.format("interface.information.select" + (entity.getVillagerType() == type ? "ed" : "")))
                    .enchant(entity.getVillagerType() == type).get();
        }
    }

    @Override
    public String getTitle() {
        return translation.format("interface.villager.specifications.title", entity.getName());
    }

    public static Material getMaterialOfType(Villager.Type villagerType) {
        switch (villagerType) {
            case DESERT:
                return Material.SANDSTONE;
            case JUNGLE:
                return Material.JUNGLE_LEAVES;
            case PLAINS:
                return Material.GRASS_BLOCK;
            case SAVANNA:
                return Material.ACACIA_SAPLING;
            case SNOW:
                return Material.SNOW_BLOCK;
            case SWAMP:
                return Material.LILY_PAD;
            case TAIGA:
                return Material.DARK_OAK_LOG;
        }

        return null;
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot >= 9 && rawSlot < Villager.Profession.values().length + 9) {
            if (entity.getProfession() != Villager.Profession.values()[rawSlot - 9]) {
                entity.setProfession(Villager.Profession.values()[rawSlot - 9]);
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), oldView::open, 2);
            }
        } else if (rawSlot >= 36 && rawSlot < Villager.Type.values().length + 36) {
            if (entity.getVillagerType() != Villager.Type.values()[rawSlot - 36]) {
                entity.setVillagerType(Villager.Type.values()[rawSlot - 36]);
                oldView.open();
            }
        } else
            super.onClick(event, rawSlot);
    }
}