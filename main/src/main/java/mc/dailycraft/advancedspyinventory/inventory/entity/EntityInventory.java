package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.inventory.entity.information.SheepColorInventory;
import mc.dailycraft.advancedspyinventory.nms.NMSContainer;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.InformationItems;
import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.time.LocalDateTime;
import java.time.Month;

public class EntityInventory<T extends LivingEntity> extends NMSContainer {
    protected final T entity;

    public EntityInventory(Player viewer, T entity, int rows) {
        super(viewer, rows);
        this.entity = entity;
    }

    public EntityInventory(Player viewer, T entity) {
        this(viewer, entity, 2);
    }

    @Override
    public ItemStack[] getContents() {
        ItemStack[] array = new ItemStack[6];

        for (int i = 0; i < 6; i++)
            array[i] = entity.getEquipment().getItem(EquipmentSlot.values()[i]);

        return array;
    }

    @Override
    public ItemStack getItem(int index) {
        if (index >= getSize() - 17 && index <= getSize() - 14)
            return getNonNull(getContents()[-index + getSize() - 12], InformationItems.values()[-index + getSize() - 12].warning(translation));
        else if (index == getSize() - 12 || index == getSize() - 11)
            return getNonNull(getContents()[index - getSize() + 12], InformationItems.values()[index - getSize() + 12].warning(translation));
        else if (index == getSize() - 8) {
            if (Permissions.ENTITY_HEALTH.has(viewer)) {
                return new ItemStackBuilder(PotionType.INSTANT_HEAL, translation.format("interface.entity.health", entity.getHealth(), entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()))
                        .lore(Permissions.ENTITY_HEALTH_MODIFY.has(viewer), translation.format("interface.entity.health.modify"))
                        .lore(Permissions.ENTITY_HEALTH_MODIFY_MAX.has(viewer), translation.format("interface.entity.health.modify.max")).get();
            }
        } else if (index == getSize() - 7) {
            if (Permissions.ENTITY_LOCATION.has(viewer))
                return getLocationItemStack(entity.getLocation(), false);
        } else if (index == getSize() - 5)
            return new ItemStackBuilder(Material.BARRIER, Permissions.ENTITY_MODIFY.has(viewer) ? translation.format("interface.entity.clear") : translation.format("interface.entity.close")).get();
        else if (index == getSize() - 3) {
            switch (entity.getType()) {
                case SHEEP -> {
                    if (viewer.hasPermission(Permissions.ENTITY_INFORMATION.get(EntityType.SHEEP))) {
                        Sheep sheep = (Sheep) entity;

                        return new ItemStackBuilder(Material.getMaterial(sheep.getColor().name() + "_WOOL"), translation.format("interface.sheep.color", SheepColorInventory.dyeToChatColor(sheep.getColor()) + translation.format("interface.sheep.color." + sheep.getColor().name().toLowerCase()))).modifyLore(viewer, sheep.getType()).get();
                    }
                }

                case IRON_GOLEM -> {
                    if (viewer.hasPermission(Permissions.ENTITY_INFORMATION.get(EntityType.IRON_GOLEM)))
                        return new ItemStackBuilder("MHF_Golem", translation.format("interface.iron_golem." + (((IronGolem) entity).isPlayerCreated() ? "player_creation" : "natural_creation"))).get();

                }

                case FOX -> {
                    if (viewer.hasPermission(Permissions.ENTITY_INFORMATION.get(EntityType.FOX))) {
                        Fox fox = (Fox) entity;

                        return new ItemStackBuilder(fox.getFoxType() == Fox.Type.RED ? Material.SPRUCE_SAPLING : Material.SNOW_BLOCK, translation.format("interface.fox.type", translation.format("interface.fox.type." + fox.getFoxType().name().toLowerCase())))
                                .lore((fox.getFoxType() == Fox.Type.RED ? "\u25ba " : "  ") + translation.format("interface.fox.red"))
                                .lore((fox.getFoxType() == Fox.Type.SNOW ? "\u25ba " : "  ") + translation.format("interface.fox.snow"))
                                .switchLore(viewer, fox.getType()).get();
                    }
                }

                case PANDA -> {
                    if (viewer.hasPermission(Permissions.ENTITY_INFORMATION.get(EntityType.PANDA))) {
                        Panda panda = (Panda) entity;
                        return new ItemStackBuilder(Material.BAMBOO, translation.format("interface.panda.gene", panda.getMainGene()))
                                .lore(translation.format("interface.panda.hidden_gene", panda.getHiddenGene())).get();
                    }
                }

                case SLIME, MAGMA_CUBE -> {
                    if (viewer.hasPermission(Permissions.ENTITY_INFORMATION.get(EntityType.SLIME))) {
                        return new ItemStackBuilder(Material.SLIME_BLOCK, translation.format("interface.slime.size", ((Slime) entity).getSize()))
                                .modifyLore(viewer, EntityType.SLIME).get();
                    }
                }

                case SNOWMAN -> {
                    if (viewer.hasPermission(Permissions.ENTITY_INFORMATION.get(EntityType.SNOWMAN))) {
                        Snowman snowman = (Snowman) entity;

                        return new ItemStackBuilder(Material.CARVED_PUMPKIN, translation.format("interface.snowman.pumpkin"))
                                .lore((!snowman.isDerp() ? "\u25ba " : "  ") + translation.format("interface.snowman.pumpkin.yes"))
                                .lore((snowman.isDerp() ? "\u25ba " : "  ") + translation.format("interface.snowman.pumpkin.no"))
                                .switchLore(viewer, entity.getType()).get();
                    }
                }
            }
        } else if (index == getSize() - 2)
            if (entity instanceof Tameable && Permissions.ENTITY_TAMED.has(viewer)) {
                if (((Tameable) entity).isTamed()) {
                    if (((Tameable) entity).getOwner() != null)
                        return new ItemStackBuilder(((Tameable) entity).getOwner().getName(), translation.format("interface.entity.tamed", ((Tameable) entity).getOwner().getName())).get();
                    else
                        return new ItemStackBuilder("MHF_Question", translation.format("interface.entity.tamed", translation.format("interface.entity.unknown"))).get();
                } else
                    return new ItemStackBuilder(LocalDateTime.now().getMonth() == Month.OCTOBER && LocalDateTime.now().getDayOfMonth() == 31 ? "MHF_Herobrine" : "MHF_Steve", translation.format("interface.entity.untamed")).get();
            }

        return VOID_ITEM;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index >= getSize() - 17 && index <= getSize() - 14) {
            if (!stack.equals(InformationItems.values()[-index + getSize() - 12].warning(translation)))
                entity.getEquipment().setItem(EquipmentSlot.values()[-index + getSize() - 12], getContents()[-index + getSize() - 12] = stack);
        } else if (index >= getSize() - 12 && index <= getSize() - 11) {
            if (!stack.equals(InformationItems.values()[index - getSize() + 12].warning(translation)))
                entity.getEquipment().setItem(EquipmentSlot.values()[index - getSize() + 12], getContents()[index - getSize() + 12] = stack);
        }
    }

    @Override
    public String getTitle() {
        return translation.format("interface.entity.title", entity.getName());
    }

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (rawSlot >= getSize() - 17 && rawSlot <= getSize() - 14 || rawSlot == getSize() - 12 || rawSlot == getSize() - 11) {
            if (Permissions.ENTITY_MODIFY.has(viewer))
                replaceItem(event, InformationItems.values()[Math.abs(getSize() - rawSlot - 12)].warning(translation));
        } else if (rawSlot == getSize() - 8) {
            if (Permissions.ENTITY_HEALTH_MODIFY.has(viewer) && event.getClick() == ClickType.LEFT)
                Main.NMS.signInterface((CustomInventoryView) event.getView(), "health", entity.getHealth(), 0d, entity.getMaxHealth(), Double::parseDouble, entity::setHealth);
            else if (Permissions.ENTITY_HEALTH_MODIFY_MAX.has(viewer) && event.getClick() == ClickType.RIGHT)
                Main.NMS.signInterface((CustomInventoryView) event.getView(), "health.max", entity.getMaxHealth(), 0d, Double.MAX_VALUE, Double::parseDouble, entity::setMaxHealth);
        } else if (rawSlot == getSize() - 7) {
            if (Permissions.ENTITY_TELEPORT.has(viewer)) {
                viewer.teleport(entity.getLocation());
                viewer.closeInventory();
            }
        } else if (rawSlot == getSize() - 5) {
            if (Permissions.ENTITY_MODIFY.has(viewer))
                event.getInventory().clear();
            else
                viewer.closeInventory();
        } else if (rawSlot == getSize() - 3) {
            if (entity.getType() == EntityType.SHEEP && viewer.hasPermission(Permissions.ENTITY_INFORMATION_MODIFY.get(EntityType.SHEEP)))
                new SheepColorInventory(viewer, (Sheep) entity, (CustomInventoryView) event.getView()).getView().open();
            else if (entity.getType() == EntityType.FOX && viewer.hasPermission(Permissions.ENTITY_INFORMATION_MODIFY.get(EntityType.FOX)))
                ((Fox) entity).setFoxType(((Fox) entity).getFoxType() == Fox.Type.RED ? Fox.Type.SNOW : Fox.Type.RED);
            else if ((entity.getType() == EntityType.MAGMA_CUBE || entity.getType() == EntityType.SLIME) && viewer.hasPermission(Permissions.ENTITY_INFORMATION_MODIFY.get(EntityType.SLIME)))
                Main.NMS.signInterface((CustomInventoryView) event.getView(), "slime", ((Slime) entity).getSize(), 1, Integer.MAX_VALUE, Integer::parseInt, result ->
                        ((Slime) entity).setSize(result));
            else if (entity.getType() == EntityType.SNOWMAN && viewer.hasPermission(Permissions.ENTITY_INFORMATION_MODIFY.get(EntityType.SNOWMAN)))
                ((Snowman) entity).setDerp(!((Snowman) entity).isDerp());
        } else if (rawSlot >= getSize()) {
            if (Permissions.ENTITY_MODIFY.has(viewer)) {
                event.setCancelled(false);

                shift(event, getSize() - 17, InformationItems.HELMET.warning(translation), current -> current.getType().getKey().getKey().endsWith("_helmet"));
                shift(event, getSize() - 16, InformationItems.CHESTPLATE.warning(translation), current -> current.getType().getKey().getKey().endsWith("_chestplate"));
                shift(event, getSize() - 15, InformationItems.LEGGINGS.warning(translation), current -> current.getType().getKey().getKey().endsWith("_leggings"));
                shift(event, getSize() - 14, InformationItems.BOOTS.warning(translation), current -> current.getType().getKey().getKey().endsWith("_boots"));
            }
        }
    }
}