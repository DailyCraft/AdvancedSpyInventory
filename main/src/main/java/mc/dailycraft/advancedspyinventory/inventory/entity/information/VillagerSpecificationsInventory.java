package mc.dailycraft.advancedspyinventory.inventory.entity.information;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Registry;
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
                .lore(translation.format("interface.villager.profession", translation.format("interface.villager.profession." + entity.getProfession().getKey().getKey()))).get();
        contents[31] = new ItemStackBuilder(Material.OAK_LOG, translation.format("interface.villager.specifications.type"))
                .lore(translation.format("generic.type_", translation.format("interface.villager.type." + entity.getVillagerType().getKey().getKey()))).get();

        int i = 9;
        for (Villager.Profession profession : Registry.VILLAGER_PROFESSION) {
            contents[i] = new ItemStackBuilder(Main.NMS.getVillagerProfessionMaterial(profession), translation.format("interface.villager.profession." + profession.getKey().getKey()))
                    .lore(translation.format("interface.information.select" + (entity.getProfession() == profession ? "ed" : "")))
                    .enchant(entity.getProfession() == profession).get();
            i++;
        }

        i = 36;
        for (Villager.Type type : Registry.VILLAGER_TYPE) {
            contents[i] = new ItemStackBuilder(getMaterialOfType(type), translation.format("interface.villager.type." + type.getKey().getKey()))
                    .lore(translation.format("interface.information.select" + (entity.getVillagerType() == type ? "ed" : "")))
                    .enchant(entity.getVillagerType() == type).get();

            i++;
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
        if (rawSlot >= 9 && rawSlot < Villager.Profession.values().length + 9) {
            if (entity.getProfession() != Villager.Profession.values()[rawSlot - 9]) {
                entity.setProfession(Villager.Profession.values()[rawSlot - 9]);
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::openOld, 2);
            }
        } else if (rawSlot >= 36 && rawSlot < Villager.Type.values().length + 36) {
            if (entity.getVillagerType() != Villager.Type.values()[rawSlot - 36]) {
                entity.setVillagerType(Villager.Type.values()[rawSlot - 36]);
                openOld();
            }
        } else
            super.onClick(event, rawSlot);
    }
}