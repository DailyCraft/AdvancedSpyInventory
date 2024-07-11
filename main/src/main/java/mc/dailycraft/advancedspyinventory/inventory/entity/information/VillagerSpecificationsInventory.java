package mc.dailycraft.advancedspyinventory.inventory.entity.information;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.utils.ClassChange;
import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;

public class VillagerSpecificationsInventory extends InformationInventory<Villager> {
    public VillagerSpecificationsInventory(Player viewer, Villager entity, InventoryView oldView) {
        super(viewer, entity, oldView, 6);

        for (int i = 0; i < 36; ++i) {
            contents[i] = VOID_ITEM;

            if (i == 8)
                i = 26;
        }

        contents[4] = new ItemStackBuilder(Material.CRAFTING_TABLE, translation.format("interface.villager.specifications.profession"))
                .lore(translation.format("interface.villager.profession", translation.format("interface.villager.profession." + ClassChange.enumName(entity.getProfession()).toLowerCase()))).get();
        contents[31] = new ItemStackBuilder(Material.OAK_LOG, translation.format("interface.villager.specifications.type"))
                .lore(translation.format("generic.type_", translation.format("interface.villager.type." + ClassChange.enumName(entity.getVillagerType()).toLowerCase()))).get();

        for (int i = 0; i < ClassChange.enumValues(Villager.Profession.class).length; ++i) {
            Villager.Profession profession = ClassChange.enumValues(Villager.Profession.class)[i];
            contents[i + 9] = new ItemStackBuilder(Main.NMS.getVillagerProfessionMaterial(profession), translation.format("interface.villager.profession." + ClassChange.enumName(profession).toLowerCase()))
                    .lore(translation.format("interface.information.select" + (entity.getProfession() == profession ? "ed" : "")))
                    .enchant(entity.getProfession() == profession).get();
        }

        for (int i = 0; i < ClassChange.enumValues(Villager.Type.class).length; ++i) {
            Villager.Type type = ClassChange.enumValues(Villager.Type.class)[i];
            contents[i + 36] = new ItemStackBuilder(getMaterialOfType(type), translation.format("interface.villager.type." + ClassChange.enumName(type).toLowerCase()))
                    .lore(translation.format("interface.information.select" + (entity.getVillagerType() == type ? "ed" : "")))
                    .enchant(entity.getVillagerType() == type).get();
        }
    }

    @Override
    public String getTitle() {
        return translation.format("interface.villager.specifications.title", entity.getName());
    }

    public static Material getMaterialOfType(Villager.Type villagerType) {
        if (villagerType == Villager.Type.DESERT)
            return Material.SANDSTONE;
        else if (villagerType == Villager.Type.JUNGLE)
            return Material.JUNGLE_LEAVES;
        else if (villagerType == Villager.Type.PLAINS)
            return Material.GRASS_BLOCK;
        else if (villagerType == Villager.Type.SAVANNA)
            return Material.ACACIA_SAPLING;
        else if (villagerType == Villager.Type.SNOW)
            return Material.SNOW_BLOCK;
        else if (villagerType == Villager.Type.SWAMP)
            return Material.LILY_PAD;
        else if (villagerType == Villager.Type.TAIGA)
            return Material.DARK_OAK_LOG;
        else
            return Material.OAK_LOG;
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot >= 9 && rawSlot < ClassChange.enumValues(Villager.Profession.class).length + 9) {
            if (entity.getProfession() != ClassChange.enumValues(Villager.Profession.class)[rawSlot - 9]) {
                entity.setProfession(ClassChange.enumValues(Villager.Profession.class)[rawSlot - 9]);
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::openOld, 2);
            }
        } else if (rawSlot >= 36 && rawSlot < ClassChange.enumValues(Villager.Type.class).length + 36) {
            if (entity.getVillagerType() != ClassChange.enumValues(Villager.Type.class)[rawSlot - 36]) {
                entity.setVillagerType(ClassChange.enumValues(Villager.Type.class)[rawSlot - 36]);
                openOld();
            }
        } else
            super.onClick(event, rawSlot);
    }
}