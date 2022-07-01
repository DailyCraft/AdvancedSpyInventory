package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.inventory.entity.information.SheepColorInventory;
import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.*;
import org.bukkit.DyeColor;
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

public class EntityInventory<T extends LivingEntity> extends BaseInventory {
    public final T entity;

    public EntityInventory(Player viewer, T entity, int rows) {
        super(viewer, rows);
        this.entity = entity;
    }

    public EntityInventory(Player viewer, T entity) {
        this(viewer, entity, 2);
    }

    @Override
    public ItemStack getItem(int index) {
        if (index >= getSize() - 17 && index <= getSize() - 14 || index == getSize() - 12 || index == getSize() - 11)
            return getNonNull(entity.getEquipment().getItem(EquipmentSlot.values()[Math.abs(index - getSize() + 12)]), InformationItems.values()[Math.abs(index - getSize() + 12)].warning(translation));
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
                case SHEEP:
                    if (Permissions.hasPermission(EntityType.SHEEP, viewer)) {
                        Sheep sheep = (Sheep) entity;
                        return new ItemStackBuilder(Material.getMaterial(sheep.getColor().name() + "_WOOL"), translation.format("interface.sheep.color", SheepColorInventory.dyeToChatColor(sheep.getColor()) + translation.format("interface.sheep.color." + sheep.getColor().name().toLowerCase()))).modifyLore(viewer, sheep.getType()).get();
                    }

                    break;

                case IRON_GOLEM:
                    if (Permissions.hasPermission(EntityType.IRON_GOLEM, viewer))
                        return new ItemStackBuilder("MHF_Golem", translation.format("interface.iron_golem." + (((IronGolem) entity).isPlayerCreated() ? "player_creation" : "natural_creation"))).get();

                    break;

                case FOX:
                    if (Permissions.hasPermission(EntityType.FOX, viewer)) {
                        Fox fox = (Fox) entity;
                        return new ItemStackBuilder(fox.getFoxType() == Fox.Type.RED ? Material.SPRUCE_SAPLING : Material.SNOW_BLOCK, translation.format("interface.fox.type", translation.format("interface.fox.type." + fox.getFoxType().name().toLowerCase())))
                                .lore((fox.getFoxType() == Fox.Type.RED ? "\u25ba " : "  ") + translation.format("interface.fox.red"))
                                .lore((fox.getFoxType() == Fox.Type.SNOW ? "\u25ba " : "  ") + translation.format("interface.fox.snow"))
                                .switchLore(viewer, fox.getType()).get();
                    }

                    break;

                case PANDA:
                    if (Permissions.hasPermission(EntityType.PANDA, viewer)) {
                        Panda panda = (Panda) entity;
                        return new ItemStackBuilder(Material.BAMBOO, translation.format("interface.panda.gene", panda.getMainGene()))
                                .lore(translation.format("interface.panda.hidden_gene", panda.getHiddenGene())).get();
                    }

                    break;

                case SLIME:
                case MAGMA_CUBE:
                    if (Permissions.hasPermission(EntityType.SLIME, viewer)) {
                        return new ItemStackBuilder(Material.SLIME_BLOCK, translation.format("interface.slime.size", ((Slime) entity).getSize()))
                                .modifyLore(viewer, EntityType.SLIME).get();
                    }

                    break;

                case SNOWMAN:
                    if (Permissions.hasPermission(EntityType.SNOWMAN, viewer)) {
                        Snowman snowman = (Snowman) entity;
                        return new ItemStackBuilder(Material.CARVED_PUMPKIN, translation.format("interface.snowman.pumpkin"))
                                .lore((!snowman.isDerp() ? "\u25ba " : "  ") + translation.format("interface.snowman.pumpkin.yes"))
                                .lore((snowman.isDerp() ? "\u25ba " : "  ") + translation.format("interface.snowman.pumpkin.no"))
                                .switchLore(viewer, entity.getType()).get();
                    }

                    break;

                case WOLF:
                    if (Permissions.hasPermission(EntityType.WOLF, viewer)) {
                        Wolf wolf = (Wolf) entity;
                        return new ItemStackBuilder(Material.BONE, translation.format("interface.wolf.angry", translation.format("interface.snowman.pumpkin." + (wolf.isAngry() ? "yes" : "no"))))
                                .lore(Permissions.hasPermissionModify(EntityType.WOLF, entity), translation.format("interface.wolf.angry.modify"))
                                .lore("")
                                .lore(translation.format("interface.wolf.collar", SheepColorInventory.dyeToChatColor(wolf.getCollarColor()) + translation.format("interface.sheep.color." + wolf.getCollarColor().name().toLowerCase())))
                                .get();
                    }

                    break;

                case OCELOT:
                    if (Permissions.hasPermission(EntityType.OCELOT, viewer)) {
                        Ocelot ocelot = (Ocelot) entity;
                        return new ItemStackBuilder(Material.TROPICAL_FISH, translation.format("interface.ocelot.trusting", translation.format("interface.snowman.pumpkin." + (ocelot.isTrusting() ? "yes" : "no"))))
                                .switchLore(viewer, EntityType.OCELOT)
                                .get();
                    }

                    break;

                case CAT:
                    if (Permissions.hasPermission(EntityType.CAT, viewer)) {
                        Cat cat = (Cat) entity;
                        ItemStackBuilder builder = new ItemStackBuilder(Material.TROPICAL_FISH, translation.format("interface.cat.type"));

                        for (Cat.Type type : Cat.Type.values())
                            builder.lore((cat.getCatType() == type ? "\u25ba " : "  ") + translation.format("interface.cat.type." + type.name().toLowerCase()));

                        return builder
                                .switchLore(viewer, EntityType.CAT)
                                .lore("")
                                .lore(translation.format("interface.wolf.collar", SheepColorInventory.dyeToChatColor(cat.getCollarColor()) + translation.format("interface.sheep.color." + cat.getCollarColor().name().toLowerCase())))
                                .get();
                    }

                    break;

                case PHANTOM:
                    if (Permissions.hasPermission(EntityType.PHANTOM, viewer)) {
                        Phantom phantom = (Phantom) entity;
                        return new ItemStackBuilder(Material.PHANTOM_MEMBRANE, translation.format("interface.phantom.size", phantom.getSize())).modifyLore(viewer, EntityType.PHANTOM).get();
                    }

                    break;

                case BAT:
                    if (Permissions.hasPermission(EntityType.BAT, viewer)) {
                        return new ItemStackBuilder(Material.LEATHER, translation.format("interface.bat.awake", translation.format("interface.snowman.pumpkin." + (((Bat) entity).isAwake() ? "yes" : "no"))))
                                .switchLore(viewer, EntityType.BAT).get();
                    }

                    break;

                case MUSHROOM_COW:
                    if (Permissions.hasPermission(EntityType.MUSHROOM_COW, viewer)) {
                        MushroomCow cow = (MushroomCow) entity;
                        boolean isRed = cow.getVariant() == MushroomCow.Variant.RED;
                        return new ItemStackBuilder(isRed ? Material.RED_MUSHROOM_BLOCK : Material.BROWN_MUSHROOM_BLOCK, translation.format("interface.mooshroom.variant", SheepColorInventory.dyeToChatColor(isRed ? DyeColor.RED : DyeColor.BROWN) + translation.format("interface.sheep.color." + (isRed ? DyeColor.RED : DyeColor.BROWN).name().toLowerCase())))
                                .switchLore(viewer, EntityType.MUSHROOM_COW)
                                .get();
                    }

                    break;

                case RABBIT:
                    if (Permissions.hasPermission(EntityType.RABBIT, viewer)) {
                        ItemStackBuilder builder = new ItemStackBuilder(Material.RABBIT_HIDE, translation.format("interface.rabbit.type"));

                        for (Rabbit.Type type : Rabbit.Type.values())
                            builder.lore((((Rabbit) entity).getRabbitType() == type ? "\u25ba " : "  ") + SheepColorInventory.dyeToChatColor(getRabbitColor(type)) + translation.format("interface.rabbit.type." + type.name().toLowerCase()));

                        return builder.switchLore(viewer, EntityType.RABBIT).get();
                    }

                    break;

                case GOAT:
                    if (Permissions.hasPermission(EntityType.GOAT, viewer))
                        return new ItemStackBuilder(Material.SCULK_SENSOR, translation.format("interface.goat.screaming", translation.format("interface.snowman.pumpkin." + (((Goat) entity).isScreaming() ? "yes" : "no")))).get();

                    break;

                case AXOLOTL:
                    if (Permissions.hasPermission(EntityType.AXOLOTL, viewer)) {
                        Axolotl axolotl = (Axolotl) entity;
                        ItemStackBuilder builder = new ItemStackBuilder(Material.getMaterial(getAxolotlColor(axolotl.getVariant()).name() + "_WOOL"), translation.format("interface.axolotl.variant"));

                        for (Axolotl.Variant variant : Axolotl.Variant.values())
                            builder.lore((axolotl.getVariant() == variant ? "\u25ba " : "  ") + SheepColorInventory.dyeToChatColor(getAxolotlColor(variant)) + translation.format("interface.axolotl.variant." + variant.name().toLowerCase()));

                        return builder.switchLore(viewer, EntityType.AXOLOTL).get();
                    }

                    break;
            }
        } else if (index == getSize() - 2) {
            if (entity instanceof Tameable && Permissions.ENTITY_TAMED.has(viewer)) {
                if (((Tameable) entity).isTamed()) {
                    if (((Tameable) entity).getOwner() != null)
                        return new ItemStackBuilder(((Tameable) entity).getOwner().getName(), translation.format("interface.entity.tamed", ((Tameable) entity).getOwner().getName())).get();
                    else
                        return new ItemStackBuilder("MHF_Question", translation.format("interface.entity.tamed", translation.format("interface.entity.unknown"))).get();
                } else
                    return new ItemStackBuilder(LocalDateTime.now().getMonth() == Month.OCTOBER && LocalDateTime.now().getDayOfMonth() == 31 ? "MHF_Herobrine" : "MHF_Question", translation.format("interface.entity.untamed")).get();
            } else if (Main.VERSION >= 17 && entity.getType() == EntityType.AXOLOTL) {
                if (Permissions.hasPermission(EntityType.AXOLOTL, viewer))
                    return new ItemStackBuilder(Material.SKELETON_SKULL, translation.format("interface.axolotl.dead", translation.format("interface.snowman.pumpkin." + (((Axolotl) entity).isPlayingDead() ? "yes" : "no")))).get();
            } else if (Main.VERSION >= 19 && entity.getType() == EntityType.GOAT) {
                if (Permissions.hasPermission(EntityType.GOAT, viewer)) {
                    return new ItemStackBuilder(Material.GOAT_HORN, translation.format("interface.goat.horn"))
                            .lore(translation.format("interface.goat.horn.left", translation.format("interface.snowman.pumpkin." + (((Goat) entity).hasLeftHorn() ? "yes" : "no"))))
                            .lore(translation.format("interface.goat.horn.right", translation.format("interface.snowman.pumpkin." + (((Goat) entity).hasRightHorn() ? "yes" : "no")))).get();
                }
            }
        }

        return VOID_ITEM;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index >= getSize() - 17 && index <= getSize() - 14 || index == getSize() - 12 || index == getSize() - 11)
            if (!stack.equals(InformationItems.values()[Math.abs(index - getSize() + 12)].warning(translation)))
                entity.getEquipment().setItem(EquipmentSlot.values()[Math.abs(index - getSize() + 12)], stack);
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
                Main.NMS.signInterface((CustomInventoryView) event.getView(), "health", entity.getHealth(), 0d, entity.getMaxHealth(), Double::parseDouble, health -> {
                    entity.setHealth(health);

                    if (entity.isDead()) {
                        viewer.closeInventory();
                        viewer.sendMessage(translation.format("interface.dead"));
                        return false;
                    } else
                        return true;
                });
            else if (Permissions.ENTITY_HEALTH_MODIFY_MAX.has(viewer) && event.getClick() == ClickType.RIGHT)
                Main.NMS.signInterface((CustomInventoryView) event.getView(), "health.max", entity.getMaxHealth(), 0.1, Double.MAX_VALUE, Double::parseDouble, maxHealth -> {
                    if (entity.isDead()) {
                        viewer.closeInventory();
                        viewer.sendMessage(translation.format("interface.dead"));
                        return false;
                    } else {
                        entity.setMaxHealth(maxHealth);
                        return true;
                    }
                });
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
            if (Permissions.hasPermissionModify(EntityType.SHEEP, viewer, entity))
                new SheepColorInventory(viewer, (Sheep) entity, (CustomInventoryView) event.getView()).getView().open();
            else if (Permissions.hasPermissionModify(EntityType.FOX, viewer, entity))
                ((Fox) entity).setFoxType(((Fox) entity).getFoxType() == Fox.Type.RED ? Fox.Type.SNOW : Fox.Type.RED);
            else if ((entity.getType() == EntityType.MAGMA_CUBE || entity.getType() == EntityType.SLIME) && Permissions.hasPermissionModify(EntityType.SLIME, viewer))
                Main.NMS.signInterface((CustomInventoryView) event.getView(), "slime", ((Slime) entity).getSize(), 1, Integer.MAX_VALUE, Integer::parseInt, result -> {
                    if (entity.isDead()) {
                        viewer.closeInventory();
                        viewer.sendMessage(translation.format("interface.dead"));
                        return false;
                    } else {
                        ((Slime) entity).setSize(result);
                        return true;
                    }
                });
            else if (Permissions.hasPermissionModify(EntityType.SNOWMAN, viewer, entity))
                ((Snowman) entity).setDerp(!((Snowman) entity).isDerp());
            else if (Permissions.hasPermissionModify(EntityType.WOLF, viewer, entity))
                ((Wolf) entity).setAngry(!((Wolf) entity).isAngry());
            else if (Permissions.hasPermissionModify(EntityType.OCELOT, viewer, entity))
                ((Ocelot) entity).setTrusting(!((Ocelot) entity).isTrusting());
            else if (Permissions.hasPermissionModify(EntityType.CAT, viewer, entity)) {
                int i = ((Cat) entity).getCatType().ordinal() + 1;
                ((Cat) entity).setCatType(Cat.Type.values()[i >= Cat.Type.values().length ? 0 : i]);
            } else if (Permissions.hasPermissionModify(EntityType.PHANTOM, viewer, entity)) {
                Main.NMS.signInterface((CustomInventoryView) event.getView(), "phantom", ((Phantom) entity).getSize(), 0, 64, Integer::parseInt, result -> {
                    if (entity.isDead()) {
                        viewer.closeInventory();
                        viewer.sendMessage(translation.format("interface.dead"));
                        return false;
                    } else {
                        ((Phantom) entity).setSize(result);
                        return true;
                    }
                });
            } else if (Permissions.hasPermissionModify(EntityType.BAT, viewer, entity))
                ((Bat) entity).setAwake(!((Bat) entity).isAwake());
            else if (Permissions.hasPermissionModify(EntityType.MUSHROOM_COW, viewer, entity))
                ((MushroomCow) entity).setVariant(((MushroomCow) entity).getVariant() == MushroomCow.Variant.RED ? MushroomCow.Variant.BROWN : MushroomCow.Variant.RED);
            else if (Permissions.hasPermissionModify(EntityType.RABBIT, viewer, entity)) {
                int i = ((Rabbit) entity).getRabbitType().ordinal() + 1;
                ((Rabbit) entity).setRabbitType(Rabbit.Type.values()[i >= Rabbit.Type.values().length ? 0 : i]);
            } else if (Main.VERSION >= 17 && Permissions.hasPermissionModify(EntityType.GOAT, viewer, entity))
                ((Goat) entity).setScreaming(!((Goat) entity).isScreaming());
            else if (Main.VERSION >= 17 && Permissions.hasPermissionModify(EntityType.AXOLOTL, viewer, entity)) {
                int i = ((Axolotl) entity).getVariant().ordinal() + 1;
                ((Axolotl) entity).setVariant(Axolotl.Variant.values()[i >= Axolotl.Variant.values().length ? 0 : i]);
            }
        } else if (rawSlot == getSize() - 2) {
            if (Main.VERSION >= 19 && Permissions.hasPermissionModify(EntityType.GOAT, viewer, entity)) {
                Goat goat = (Goat) entity;

                if (event.getClick() == ClickType.LEFT)
                    goat.setLeftHorn(!goat.hasLeftHorn());
                else if (event.getClick() == ClickType.RIGHT)
                    goat.setRightHorn(!goat.hasRightHorn());
            }
        } else if (rawSlot >= getSize()) {
            if (Permissions.ENTITY_MODIFY.has(viewer)) {
                event.setCancelled(false);

                shift(event, getSize() - 17, InformationItems.HELMET.warning(translation), current -> current.isItem() && current.getEquipmentSlot() == EquipmentSlot.HEAD);
                shift(event, getSize() - 16, InformationItems.CHESTPLATE.warning(translation), current -> current.isItem() && current.getEquipmentSlot() == EquipmentSlot.CHEST);
                shift(event, getSize() - 15, InformationItems.LEGGINGS.warning(translation), current -> current.isItem() && current.getEquipmentSlot() == EquipmentSlot.LEGS);
                shift(event, getSize() - 14, InformationItems.BOOTS.warning(translation), current -> current.isItem() && current.getEquipmentSlot() == EquipmentSlot.FEET);
            }
        }
    }

    private static DyeColor getAxolotlColor(Axolotl.Variant variant) {
        switch (variant) {
            case LUCY:
                return DyeColor.PINK;
            case WILD:
                return DyeColor.BROWN;
            case GOLD:
                return DyeColor.YELLOW;
            case CYAN:
                return DyeColor.CYAN;
            case BLUE:
                return DyeColor.BLUE;
        }

        return null;
    }

    private static DyeColor getRabbitColor(Rabbit.Type rabbitType) {
        switch (rabbitType) {
            case BROWN:
                return DyeColor.BROWN;
            case WHITE:
                return DyeColor.WHITE;
            case BLACK:
                return DyeColor.BLACK;
            case BLACK_AND_WHITE:
                return DyeColor.GRAY;
            case GOLD:
                return DyeColor.YELLOW;
            case SALT_AND_PEPPER:
                return DyeColor.ORANGE;
            case THE_KILLER_BUNNY:
                return DyeColor.RED;
        }

        return null;
    }
}