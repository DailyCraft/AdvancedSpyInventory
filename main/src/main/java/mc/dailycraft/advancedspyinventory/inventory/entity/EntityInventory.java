package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.inventory.entity.information.SheepColorInventory;
import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.inventory.entity.information.VillagerSpecificationsInventory;
import mc.dailycraft.advancedspyinventory.utils.*;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import javax.naming.NoPermissionException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityInventory<T extends LivingEntity> extends BaseInventory {
    private static final Map<EntityType, DataItem<?>> DATA_ITEMS = new HashMap<>();
    public final T entity;
    protected @Nullable InventorySize inventorySize;
    private int villagerTick = 0;

    public EntityInventory(Player viewer, T entity, int rows) {
        super(viewer, rows);
        this.entity = entity;
        this.inventorySize = entity instanceof InventoryHolder ? InventorySize.values()[((InventoryHolder) entity).getInventory().getSize() - 1] : null;
    }

    @Override
    public int getSize() {
        return super.getSize() + (inventorySize != null ? inventorySize.getNecessaryRows() * 9 : 0);
    }

    public EntityInventory(Player viewer, T entity) {
        this(viewer, entity, 2);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ItemStack getItem(int index) {
        if (inventorySize != null && inventorySize.hasSlot(index))
            return ((InventoryHolder) entity).getInventory().getItem(inventorySize.toSlot(index));

        if (index >= getSize() - 17 && index <= getSize() - 14 || index == getSize() - 12 || index == getSize() - 11)
            return getNonNull(getEquipmentItem(EquipmentSlot.values()[Math.abs(index - getSize() + 12)]), InformationItems.values()[Math.abs(index - getSize() + 12)].warning(translation));

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
            EntityType type = entity.getType();

            if (type == EntityType.MAGMA_CUBE)
                type = EntityType.SLIME;

            DataItem<LivingEntity> dataItem = (DataItem<LivingEntity>) DATA_ITEMS.get(type);

            if (dataItem != null) {
                try {
                    return dataItem.get((EntityInventory<LivingEntity>) this, type, viewer);
                } catch (NoPermissionException ignored) {
                }
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
        if (inventorySize != null && inventorySize.hasSlot(index))
            ((InventoryHolder) entity).getInventory().setItem(inventorySize.toSlot(index), stack);

        if (index >= getSize() - 17 && index <= getSize() - 14 || index == getSize() - 12 || index == getSize() - 11)
            if (!stack.equals(InformationItems.values()[Math.abs(index - getSize() + 12)].warning(translation)))
                setEquipmentItem(EquipmentSlot.values()[Math.abs(index - getSize() + 12)], stack);
    }

    @Override
    public String getTitle() {
        return translation.format("interface.entity.title", entity.getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (inventorySize != null && inventorySize.hasSlot(rawSlot) && Permissions.ENTITY_MODIFY.has(viewer))
            event.setCancelled(false);

        if (rawSlot >= getSize() - 17 && rawSlot <= getSize() - 14 || rawSlot == getSize() - 12 || rawSlot == getSize() - 11) {
            if (Permissions.ENTITY_MODIFY.has(viewer))
                replaceItem(event, InformationItems.values()[Math.abs(getSize() - rawSlot - 12)].warning(translation));
        } else if (rawSlot == getSize() - 8) {
            if (Permissions.ENTITY_HEALTH_MODIFY.has(viewer) && event.getClick() == ClickType.LEFT)
                Main.NMS.signInterface((CustomInventoryView) event.getView(), "health", entity.getHealth(), 0d, entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), Double::parseDouble, health -> {
                    entity.setHealth(health);

                    if (entity.isDead()) {
                        viewer.closeInventory();
                        viewer.sendMessage(translation.format("interface.dead"));
                        return false;
                    } else
                        return true;
                });
            else if (Permissions.ENTITY_HEALTH_MODIFY_MAX.has(viewer) && event.getClick() == ClickType.RIGHT)
                Main.NMS.signInterface((CustomInventoryView) event.getView(), "health.max", entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), 0.1, Double.MAX_VALUE, Double::parseDouble, maxHealth -> {
                    if (entity.isDead()) {
                        viewer.closeInventory();
                        viewer.sendMessage(translation.format("interface.dead"));
                        return false;
                    } else {
                        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
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
            EntityType type = entity.getType();

            if (type == EntityType.MAGMA_CUBE)
                type = EntityType.SLIME;

            DataItem<LivingEntity> dataItem = (DataItem<LivingEntity>) DATA_ITEMS.get(type);

            if (dataItem != null)
                dataItem.click((EntityInventory<LivingEntity>) this, event, type, viewer);
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

                shift(event, getSize() - 17, InformationItems.HELMET.warning(translation), current -> Main.VERSION > 16 ? current.getEquipmentSlot() == EquipmentSlot.HEAD : current.getKey().getKey().endsWith("_helmet"));
                shift(event, getSize() - 16, InformationItems.CHESTPLATE.warning(translation), current -> Main.VERSION > 16 ? current.getEquipmentSlot() == EquipmentSlot.CHEST : current.getKey().getKey().endsWith("_chestplate"));
                shift(event, getSize() - 15, InformationItems.LEGGINGS.warning(translation), current -> Main.VERSION > 16 ? current.getEquipmentSlot() == EquipmentSlot.LEGS : current.getKey().getKey().endsWith("_leggings"));
                shift(event, getSize() - 14, InformationItems.BOOTS.warning(translation), current -> Main.VERSION > 16 ? current.getEquipmentSlot() == EquipmentSlot.FEET : current.getKey().getKey().endsWith("_boots"));
            }
        }
    }

    static {
        //<editor-fold desc="Data Items - All versions" defaultstate="collapsed">
        DATA_ITEMS.put(EntityType.BAT, new DataItem<Bat>((inv, entity) ->
                new ItemStackBuilder(Material.LEATHER, inv.translation.format("interface.bat.awake", inv.translation.format("interface.snowman.pumpkin." + (entity.isAwake() ? "yes" : "no"))))
                        .switchLore(inv.viewer, EntityType.BAT).get(),
                (inv, event, entity) ->
                        entity.setAwake(!entity.isAwake())));

        DATA_ITEMS.put(EntityType.IRON_GOLEM, new DataItem<IronGolem>((inv, entity) ->
                new ItemStackBuilder("MHF_Golem", inv.translation.format("interface.iron_golem." + (entity.isPlayerCreated() ? "player_creation" : "natural_creation"))).get(),
                null));

        DATA_ITEMS.put(EntityType.OCELOT, new DataItem<Ocelot>((inv, entity) ->
                new ItemStackBuilder(Material.TROPICAL_FISH, inv.translation.format("interface.ocelot.trusting", inv.translation.format("interface.snowman.pumpkin." + (Main.NMS.isOcelotTrusting(entity) ? "yes" : "no"))))
                        .switchLore(inv.viewer, EntityType.OCELOT).get(),
                (inv, event, entity) ->
                        Main.NMS.setOcelotTrusting(entity, !Main.NMS.isOcelotTrusting(entity))));

        DATA_ITEMS.put(EntityType.PHANTOM, new DataItem<Phantom>((inv, entity) ->
                new ItemStackBuilder(Material.PHANTOM_MEMBRANE, inv.translation.format("interface.phantom.size", entity.getSize()))
                        .modifyLore(inv.viewer, EntityType.PHANTOM).get(),
                (inv, event, entity) ->
                        Main.NMS.signInterface((CustomInventoryView) event.getView(), "phantom", entity.getSize(), 0, 64, Integer::parseInt, result -> {
                            if (entity.isDead()) {
                                inv.viewer.closeInventory();
                                inv.viewer.sendMessage(inv.translation.format("interface.dead"));
                                return false;
                            } else {
                                entity.setSize(result);
                                return true;
                            }
                        })));

        DATA_ITEMS.put(EntityType.RABBIT, new DataItem<Rabbit>((inv, entity) -> {
            ItemStackBuilder builder = new ItemStackBuilder(Material.RABBIT_HIDE, inv.translation.format("interface.rabbit.type"));

            for (Rabbit.Type type : Rabbit.Type.values())
                builder.lore((entity.getRabbitType() == type ? "\u25ba " : "  ") + dyeToChatColor(getRabbitColor(type)) + inv.translation.format("interface.rabbit.type." + type.name().toLowerCase()));

            return builder.switchLore(inv.viewer, EntityType.RABBIT).get();
        }, (inv, event, entity) -> {
            int i = entity.getRabbitType().ordinal() + 1;
            entity.setRabbitType(Rabbit.Type.values()[i >= Rabbit.Type.values().length ? 0 : i]);
        }));

        DATA_ITEMS.put(EntityType.SHEEP, new DataItem<Sheep>((inv, entity) ->
                new ItemStackBuilder(entity.getColor() != null ? Material.getMaterial(entity.getColor().name() + "_WOOL") : Material.WHITE_WOOL, inv.translation.format("interface.sheep.color", dyeToChatColor(entity.getColor()) + inv.translation.format("interface.sheep.color." + entity.getColor().name().toLowerCase())))
                        .modifyLore(inv.viewer, entity.getType()).get(),
                (inv, event, entity) ->
                        new SheepColorInventory(inv.viewer, entity, (CustomInventoryView) event.getView()).getView().open()));

        DATA_ITEMS.put(EntityType.SLIME, new DataItem<Slime>((inv, entity) ->
                new ItemStackBuilder(Material.SLIME_BLOCK, inv.translation.format("interface.slime.size", entity.getSize()))
                        .modifyLore(inv.viewer, EntityType.SLIME).get(),
                (inv, event, entity) ->
                        Main.NMS.signInterface((CustomInventoryView) event.getView(), "slime", entity.getSize(), 1, Integer.MAX_VALUE, Integer::parseInt, result -> {
                            if (entity.isDead()) {
                                inv.viewer.closeInventory();
                                inv.viewer.sendMessage(inv.translation.format("interface.dead"));
                                return false;
                            } else {
                                entity.setSize(result);
                                return true;
                            }
                        })));

        DATA_ITEMS.put(EntityType.SNOWMAN, new DataItem<Snowman>((inv, entity) ->
                new ItemStackBuilder(Material.CARVED_PUMPKIN, inv.translation.format("interface.snowman.pumpkin"))
                        .lore((!entity.isDerp() ? "\u25ba " : "  ") + inv.translation.format("interface.snowman.pumpkin.yes"))
                        .lore((entity.isDerp() ? "\u25ba " : "  ") + inv.translation.format("interface.snowman.pumpkin.no"))
                        .switchLore(inv.viewer, entity.getType()).get(),
                (inv, event, entity) ->
                        entity.setDerp(!entity.isDerp())));

        DATA_ITEMS.put(EntityType.VILLAGER, new DataItem<Villager>((inv, entity) -> {
            if (Main.VERSION < 14) {
                ItemStackBuilder builder = new ItemStackBuilder("MHF_Villager", inv.translation.format("interface.villager.profession", inv.translation.format("interface.villager.profession." + Main.NMS.getVillagerProfession(entity).toLowerCase().replace("_", ""))));

                for (String profession : Main.NMS.getVillagerProfessions())
                    builder.lore((Main.NMS.getVillagerProfession(entity).equals(profession) ? "\u25ba " : "  ") + inv.translation.format("interface.villager.profession." + profession.toLowerCase().replace("_", "")));

                return builder.switchLore(inv.viewer, EntityType.VILLAGER).get();
            } else {
                if (inv.villagerTick >= 80)
                    inv.villagerTick = 0;

                return new ItemStackBuilder(++inv.villagerTick < 40 ? Main.NMS.getVillagerProfessionMaterial(entity.getProfession()) : VillagerSpecificationsInventory.getMaterialOfType(entity.getVillagerType()), inv.translation.format("interface.villager.specifications"))
                        .lore(inv.translation.format("interface.villager.profession", (inv.villagerTick < 40 ? "§l" : "") + inv.translation.format("interface.villager.profession." + entity.getProfession().name().toLowerCase())))
                        .lore(inv.translation.format("interface.villager.type", (inv.villagerTick >= 40 ? "§l" : "") + inv.translation.format("interface.villager.type." + entity.getVillagerType().name().toLowerCase()))).get();
            }
        }, (inv, event, entity) -> {
            if (Main.VERSION < 14) {
                String[] professions = Main.NMS.getVillagerProfessions();
                String profession = Main.NMS.getVillagerProfession(entity);

                for (int i = 0; i < professions.length; ++i) {
                    if (profession.equals(professions[i])) {
                        String profession1 = professions[i + 1 >= professions.length ? 0 : i + 1];
                        Main.NMS.setVillagerProfession(entity, profession1);
                        break;
                    }
                }
            } else
                new VillagerSpecificationsInventory(inv.viewer, entity, (CustomInventoryView) event.getView()).getView().open();
        }));

        DATA_ITEMS.put(EntityType.WOLF, new DataItem<Wolf>((inv, entity) ->
                new ItemStackBuilder(Material.BONE, inv.translation.format("interface.wolf.angry", inv.translation.format("interface.snowman.pumpkin." + (entity.isAngry() ? "yes" : "no"))))
                        .lore(Permissions.hasPermissionModify(EntityType.WOLF, entity), inv.translation.format("interface.wolf.angry.modify"))
                        .lore("")
                        .lore(inv.translation.format("interface.wolf.collar", dyeToChatColor(entity.getCollarColor()) + inv.translation.format("interface.sheep.color." + entity.getCollarColor().name().toLowerCase())))
                        .get(),
                (inv, event, entity) ->
                        entity.setAngry(!entity.isAngry())));
        //</editor-fold>

        if (Main.VERSION >= 14) {
            //<editor-fold desc="Data Items - 1.14+ versions" defaultstate="collapsed">
            DATA_ITEMS.put(EntityType.CAT, new DataItem<Cat>((inv, entity) -> {
                ItemStackBuilder builder = new ItemStackBuilder(Material.TROPICAL_FISH, inv.translation.format("interface.cat.type"));

                for (Cat.Type type : Cat.Type.values())
                    builder.lore((entity.getCatType() == type ? "\u25ba " : "  ") + inv.translation.format("interface.cat.type." + type.name().toLowerCase()));

                return builder
                        .switchLore(inv.viewer, EntityType.CAT)
                        .lore("")
                        .lore(inv.translation.format("interface.wolf.collar", dyeToChatColor(entity.getCollarColor()) + inv.translation.format("interface.sheep.color." + entity.getCollarColor().name().toLowerCase())))
                        .get();
            }, (inv, event, entity) -> {
                int i = entity.getCatType().ordinal() + 1;
                entity.setCatType(Cat.Type.values()[i >= Cat.Type.values().length ? 0 : i]);
            }));

            DATA_ITEMS.put(EntityType.FOX, new DataItem<Fox>((inv, entity) ->
                    new ItemStackBuilder(entity.getFoxType() == Fox.Type.RED ? Material.SPRUCE_SAPLING : Material.SNOW_BLOCK, inv.translation.format("interface.fox.type", inv.translation.format("interface.fox.type." + entity.getFoxType().name().toLowerCase())))
                            .lore((entity.getFoxType() == Fox.Type.RED ? "\u25ba " : "  ") + inv.translation.format("interface.fox.red"))
                            .lore((entity.getFoxType() == Fox.Type.SNOW ? "\u25ba " : "  ") + inv.translation.format("interface.fox.snow"))
                            .switchLore(inv.viewer, entity.getType()).get(),
                    (inv, event, entity) ->
                            entity.setFoxType(entity.getFoxType() == Fox.Type.RED ? Fox.Type.SNOW : Fox.Type.RED)));

            DATA_ITEMS.put(EntityType.MUSHROOM_COW, new DataItem<MushroomCow>((inv, entity) -> {
                boolean isRed = entity.getVariant() == MushroomCow.Variant.RED;
                return new ItemStackBuilder(isRed ? Material.RED_MUSHROOM_BLOCK : Material.BROWN_MUSHROOM_BLOCK, inv.translation.format("interface.mooshroom.variant", dyeToChatColor(isRed ? DyeColor.RED : DyeColor.BROWN) + inv.translation.format("interface.sheep.color." + (isRed ? DyeColor.RED : DyeColor.BROWN).name().toLowerCase())))
                        .switchLore(inv.viewer, EntityType.MUSHROOM_COW)
                        .get();
            }, (inv, event, entity) ->
                    entity.setVariant(entity.getVariant() == MushroomCow.Variant.RED ? MushroomCow.Variant.BROWN : MushroomCow.Variant.RED)));


            DATA_ITEMS.put(EntityType.PANDA, new DataItem<Panda>((inv, entity) ->
                    new ItemStackBuilder(Material.BAMBOO, inv.translation.format("interface.panda.gene", entity.getMainGene()))
                            .lore(inv.translation.format("interface.panda.hidden_gene", entity.getHiddenGene())).get(),
                    null));
            //</editor-fold>
        }

        if (Main.VERSION >= 17) {
            //<editor-fold desc="Data Items - 1.17+ versions" defaultstate="collapsed">
            DATA_ITEMS.put(EntityType.AXOLOTL, new DataItem<Axolotl>((inv, entity) -> {
                ItemStackBuilder builder = new ItemStackBuilder(Material.getMaterial(getAxolotlColor(entity.getVariant()).name() + "_WOOL"), inv.translation.format("interface.axolotl.variant"));

                for (Axolotl.Variant variant : Axolotl.Variant.values())
                    builder.lore((entity.getVariant() == variant ? "\u25ba " : "  ") + dyeToChatColor(getAxolotlColor(variant)) + inv.translation.format("interface.axolotl.variant." + variant.name().toLowerCase()));

                return builder.switchLore(inv.viewer, EntityType.AXOLOTL).get();
            }, (inv, event, entity) -> {
                int i = entity.getVariant().ordinal() + 1;
                entity.setVariant(Axolotl.Variant.values()[i >= Axolotl.Variant.values().length ? 0 : i]);
            }));

            DATA_ITEMS.put(EntityType.GOAT, new DataItem<Goat>((inv, entity) ->
                    new ItemStackBuilder(Material.SCULK_SENSOR, inv.translation.format("interface.goat.screaming", inv.translation.format("interface.snowman.pumpkin." + (entity.isScreaming() ? "yes" : "no")))).get(),
                    (inv, event, entity) ->
                            entity.setScreaming(!entity.isScreaming())));
            //</editor-fold>
        }

        if (Main.VERSION >= 19) {
            //<editor-fold desc="Data Items - 1.19+ versions" defaultstate="collapsed">
            DATA_ITEMS.put(EntityType.ALLAY, new DataItem<Allay>((inv, entity) -> {
                UUID memory = entity.getMemory(MemoryKey.LIKED_PLAYER);

                if (memory != null) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(memory);
                    return new ItemStackBuilder(player.getName(), inv.translation.format("interface.allay.owner", player.getName())).get();
                } else
                    return new ItemStackBuilder("MHF_Question", inv.translation.format("interface.allay.unowned")).get();
            }, null));
            //</editor-fold>
        }
    }

    private ItemStack getEquipmentItem(EquipmentSlot slot) {
        EntityEquipment equipment = entity.getEquipment();

        if (equipment == null)
            return null;

        switch (slot) {
            case HEAD:
                return equipment.getHelmet();
            case CHEST:
                return equipment.getChestplate();
            case LEGS:
                return equipment.getLeggings();
            case FEET:
                return equipment.getBoots();
            case HAND:
                return equipment.getItemInMainHand();
            case OFF_HAND:
                return equipment.getItemInOffHand();
        }

        return null;
    }

    private void setEquipmentItem(EquipmentSlot slot, ItemStack stack) {
        EntityEquipment equipment = entity.getEquipment();

        if (equipment == null)
            return;

        switch (slot) {
            case HEAD:
                equipment.setHelmet(stack);
                break;
            case CHEST:
                equipment.setChestplate(stack);
                break;
            case LEGS:
                equipment.setLeggings(stack);
                break;
            case FEET:
                equipment.setBoots(stack);
                break;
            case HAND:
                equipment.setItemInMainHand(stack);
                break;
            case OFF_HAND:
                equipment.setItemInOffHand(stack);
                break;
        }
    }

    private static DyeColor getAxolotlColor(Axolotl.Variant variant) {
        if (variant == Axolotl.Variant.LUCY)
            return DyeColor.PINK;
        else if (variant == Axolotl.Variant.WILD)
            return DyeColor.BROWN;
        else if (variant == Axolotl.Variant.GOLD)
            return DyeColor.YELLOW;
        else if (variant == Axolotl.Variant.CYAN)
            return DyeColor.CYAN;
        else if (variant == Axolotl.Variant.BLUE)
            return DyeColor.BLUE;

        return DyeColor.WHITE;
    }

    private static DyeColor getRabbitColor(Rabbit.Type rabbitType) {
        switch (rabbitType) {
            case BROWN:
                return DyeColor.BROWN;
            case WHITE:
            default:
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
    }
}