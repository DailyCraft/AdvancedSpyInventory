package mc.dailycraft.advancedspyinventory.inventory.entity.information;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

public class VillagerSpecificationsInventory extends InformationInventory<Villager> {
    protected static final Field professionBlocksField;

    public VillagerSpecificationsInventory(Player viewer, Villager entity, CustomInventoryView oldView) {
        super(viewer, entity, oldView, 6);

        for (int i = 0; i < 36; ++i) {
            contents.set(i, VOID_ITEM);

            if (i == 8)
                i = 26;
        }

        contents.set(4, new ItemStackBuilder(Material.CRAFTING_TABLE, translation.format("interface.villager.specifications.profession"))
                .lore(translation.format("interface.villager.profession", translation.format("interface.villager.profession." + entity.getProfession().name().toLowerCase()))).nms());
        contents.set(31, new ItemStackBuilder(Material.OAK_LOG, translation.format("interface.villager.specifications.type"))
                .lore(translation.format("interface.villager.type", translation.format("interface.villager.type." + entity.getVillagerType().name().toLowerCase()))).nms());

        for (int i = 0; i < Villager.Profession.values().length; ++i) {
            Villager.Profession profession = Villager.Profession.values()[i];
            contents.set(i + 9, new ItemStackBuilder(getMaterialOfProfession(profession), translation.format("interface.villager.profession." + profession.name().toLowerCase()))
                    .lore(translation.format("interface.information.select" + (entity.getProfession() == profession ? "ed" : "")))
                    .enchant(entity.getProfession() == profession).nms());
        }

        for (int i = 0; i < Villager.Type.values().length; ++i) {
            Villager.Type type = Villager.Type.values()[i];
            contents.set(i + 36, new ItemStackBuilder(getMaterialOfType(type), translation.format("interface.villager.type." + type.name().toLowerCase()))
                    .lore(translation.format("interface.information.select" + (entity.getVillagerType() == type ? "ed" : "")))
                    .enchant(entity.getVillagerType() == type).nms());
        }
    }

    @Override
    public String getTitle() {
        return translation.format("interface.villager.specifications.title", entity.getName());
    }

    public static Material getMaterialOfProfession(Villager.Profession profession) {
        Material material = null;

        switch (profession) {
            case NONE:
                material = Material.BELL;
                break;

            case NITWIT:
                material = Material.OAK_DOOR;
                break;

            default:
                try {
                    Iterator<IBlockData> iterator = ((Set<IBlockData>) professionBlocksField.get(CraftVillager.bukkitToNmsProfession(profession).b())).iterator();
                    material = iterator.hasNext() ? CraftMagicNumbers.getMaterial(iterator.next().getBlock()) : Material.RED_BED;
                } catch (IllegalAccessException exception) {
                    exception.printStackTrace();
                }
        }

        return material;
    }

    public static Material getMaterialOfType(Villager.Type villagerType) {
        Material material = null;

        switch (villagerType) {
            case DESERT:
                material = Material.SANDSTONE;
                break;

            case JUNGLE:
                material = Material.JUNGLE_LEAVES;
                break;

            case PLAINS:
                material = Material.GRASS_BLOCK;
                break;

            case SAVANNA:
                material = Material.ACACIA_SAPLING;
                break;

            case SNOW:
                material = Material.SNOW_BLOCK;
                break;

            case SWAMP:
                material = Material.LILY_PAD;
                break;

            case TAIGA:
                material = Material.DARK_OAK_LOG;
                break;
        }

        return material;
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

    static {
        try {
            professionBlocksField = VillagePlaceType.class.getDeclaredField("C");
            professionBlocksField.setAccessible(true);
        } catch (NoSuchFieldException exception) {
            throw new Error(exception);
        }
    }
}