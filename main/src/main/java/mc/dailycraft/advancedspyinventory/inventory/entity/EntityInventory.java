package mc.dailycraft.advancedspyinventory.inventory.entity;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.inventory.entity.information.SheepColorInventory;
import mc.dailycraft.advancedspyinventory.inventory.entity.information.VillagerSpecificationsInventory;
import mc.dailycraft.advancedspyinventory.utils.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
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

        if (!((this.entity = entity) instanceof AbstractHorse))
            inventorySize = entity instanceof InventoryHolder ? InventorySize.values()[((InventoryHolder) entity).getInventory().getSize() - 1] : null;
    }

    @Override
    public int getSize() {
        return super.getSize() + (inventorySize != null ? inventorySize.getNecessaryRows() * 9 : 0);
    }

    public EntityInventory(Player viewer, T entity) {
        this(viewer, entity, 2);
    }

    @Override
    public ItemStack getItem(int index) {
        if (inventorySize != null && inventorySize.hasSlot(index))
            return ((InventoryHolder) entity).getInventory().getItem(inventorySize.toSlot(index));

        if (index >= getSize() - 17 && index <= getSize() - 14 || index == getSize() - 12 || index == getSize() - 11)
            return getNonNull(Main.NMS.getEquipment(entity, EquipmentSlot.values()[Math.abs(index - getSize() + 12)]), InformationItems.values()[Math.abs(index - getSize() + 12)].warning(translation));

        else if (index == getSize() - 8) {
            if (Permissions.ENTITY_HEALTH.has(viewer)) {
                return new ItemStackBuilder(PotionType.HEALING, translation.format("interface.entity.health", entity.getHealth(), entity.getAttribute(Attribute.MAX_HEALTH).getValue()))
                        .lore(Permissions.ENTITY_HEALTH_MODIFY.has(viewer) || Permissions.ENTITY_HEALTH_MODIFY_MAX.has(viewer), "", translation.format("interface.entity.health.modify.0"))
                        .lore(Permissions.ENTITY_HEALTH_MODIFY.has(viewer), "   " + translation.format("interface.entity.health.modify.1"))
                        .lore(Permissions.ENTITY_HEALTH_MODIFY_MAX.has(viewer), "   " + translation.format("interface.entity.health.modify.2")).get();
            }
        } else if (index == getSize() - 7) {
            if (Permissions.ENTITY_LOCATION.has(viewer))
                return getLocationItemStack(entity.getLocation(), false);
        } else if (index == getSize() - 5)
            return new ItemStackBuilder(Material.BARRIER, translation.format("interface.entity.close"))
                    .lore(Permissions.ENTITY_MODIFY.has(viewer), "", translation.format("interface.entity.clear"), translation.format("interface.entity.clear.warning")).get();
        else if (index == getSize() - 3) {
            EntityType type = entity.getType();

            if (type == EntityType.MAGMA_CUBE)
                type = EntityType.SLIME;

            @SuppressWarnings("unchecked")
            DataItem<T> dataItem = (DataItem<T>) DATA_ITEMS.get(type);

            if (dataItem != null) {
                try {
                    return dataItem.get(this, type, viewer);
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
            } else if (entity.getType() == EntityType.PANDA) {
                if (Permissions.hasPermission(EntityType.PANDA, viewer))
                    return new ItemStackBuilder(Material.DEBUG_STICK, formatModify("interface.panda.gene.hidden"))
                            .enumLore(translation, Panda.Gene.values(), ((Panda) entity).getHiddenGene(), "interface.panda.gene").get();
            } else if (entity.getType() == EntityType.AXOLOTL) {
                if (Permissions.hasPermission(EntityType.AXOLOTL, viewer))
                    return new ItemStackBuilder(Material.SKELETON_SKULL, formatToggleYesNo(((Axolotl) entity).isPlayingDead(), "interface.axolotl.dead")).get();
            } else if (entity.getType() == EntityType.GOAT) {
                if (Permissions.hasPermission(EntityType.GOAT, viewer)) {
                    return new ItemStackBuilder(Material.GOAT_HORN, translation.format("interface.goat.horn"))
                            .lore(ChatColor.WHITE + translation.formatYesNo(((Goat) entity).hasLeftHorn(), "interface.goat.horn.left") + translation.formatCondition(Permissions.hasPermissionModify(EntityType.GOAT, viewer), "generic.toggle.left"))
                            .lore(ChatColor.WHITE + translation.formatYesNo(((Goat) entity).hasRightHorn(), "interface.goat.horn.right") + translation.formatCondition(Permissions.hasPermissionModify(EntityType.GOAT, viewer), "generic.toggle.right")).get();
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

    @Override
    public void onClick(InventoryClickEvent event, int rawSlot) {
        if (inventorySize != null && inventorySize.hasSlot(rawSlot) && Permissions.ENTITY_MODIFY.has(viewer))
            event.setCancelled(false);

        if (rawSlot >= getSize() - 17 && rawSlot <= getSize() - 14 || rawSlot == getSize() - 12 || rawSlot == getSize() - 11) {
            if (Permissions.ENTITY_MODIFY.has(viewer))
                replaceItem(event, InformationItems.values()[Math.abs(getSize() - rawSlot - 12)].warning(translation));
        } else if (rawSlot == getSize() - 8) {
            if (Permissions.ENTITY_HEALTH_MODIFY.has(viewer) && event.isLeftClick())
                openSign("health", entity.getHealth(), 0d, entity.getAttribute(Attribute.MAX_HEALTH).getValue(), Double::parseDouble, health -> {
                    entity.setHealth(health);

                    if (entity.isDead()) {
                        viewer.closeInventory();
                        viewer.sendMessage(translation.format("interface.dead"));
                        return false;
                    } else
                        return true;
                });
            else if (Permissions.ENTITY_HEALTH_MODIFY_MAX.has(viewer) && event.isRightClick())
                openSign("health.max", entity.getAttribute(Attribute.MAX_HEALTH).getValue(), 0.1, Double.MAX_VALUE, Double::parseDouble, maxHealth -> {
                    if (entity.isDead()) {
                        viewer.closeInventory();
                        viewer.sendMessage(translation.format("interface.dead"));
                        return false;
                    } else {
                        entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);
                        return true;
                    }
                });
        } else if (rawSlot == getSize() - 7) {
            if (Permissions.ENTITY_TELEPORT.has(viewer)) {
                if (event.isShiftClick()) {
                    if (Permissions.ENTITY_TELEPORT_OTHERS.has(viewer))
                        entity.teleport(viewer);
                } else
                    viewer.teleport(entity);
            }
        } else if (rawSlot == getSize() - 5) {
            if (Permissions.ENTITY_MODIFY.has(viewer) && event.isShiftClick())
                event.getInventory().clear();
            else
                viewer.closeInventory();
        } else if (rawSlot == getSize() - 3) {
            EntityType type = entity.getType();

            if (type == EntityType.MAGMA_CUBE)
                type = EntityType.SLIME;

            @SuppressWarnings("unchecked")
            DataItem<T> dataItem = (DataItem<T>) DATA_ITEMS.get(type);

            if (dataItem != null)
                dataItem.click(this, event, type, viewer);
        } else if (rawSlot == getSize() - 2) {
            if (Permissions.hasPermissionModify(EntityType.PANDA, viewer, entity)) {
                Panda panda = (Panda) entity;
                ItemStackBuilder.enumLoreClick(event, Panda.Gene.values(), panda.getHiddenGene(), panda::setHiddenGene);
            } else if (Permissions.hasPermissionModify(EntityType.AXOLOTL, viewer, entity)) {
                ((Axolotl) entity).setPlayingDead(!((Axolotl) entity).isPlayingDead());
            } else if (Permissions.hasPermissionModify(EntityType.GOAT, viewer, entity)) {
                Goat goat = (Goat) entity;

                if (event.isLeftClick())
                    goat.setLeftHorn(!goat.hasLeftHorn());
                else if (event.isRightClick())
                    goat.setRightHorn(!goat.hasRightHorn());
            }
        } else if (rawSlot >= getSize()) {
            if (Permissions.ENTITY_MODIFY.has(viewer)) {
                event.setCancelled(false);

                shift(event, getSize() - 17, EquipmentSlot.HEAD, item -> item::warning);
                shift(event, getSize() - 16, EquipmentSlot.CHEST, item -> item::warning);
                shift(event, getSize() - 15, EquipmentSlot.LEGS, item -> item::warning);
                shift(event, getSize() - 14, EquipmentSlot.FEET, item -> item::warning);
            }
        }
    }

    static {
        //<editor-fold desc="Data Items - All versions" defaultstate="collapsed">
        DATA_ITEMS.put(EntityType.BAT, new DataItem<Bat>((inv, entity) ->
                new ItemStackBuilder(Material.LEATHER, inv.formatToggleYesNo(entity.isAwake(), "interface.bat.awake"))
                        .get(),
                (inv, event, entity) ->
                        entity.setAwake(!entity.isAwake())));

        DATA_ITEMS.put(EntityType.CREEPER, new DataItem<Creeper>((inv, entity) ->
                new ItemStackBuilder(new ItemStack(Material.CREEPER_HEAD), inv.formatToggleYesNo(entity.isPowered(), "interface.creeper.charged"))
                        .get(),
                (inv, event, entity) ->
                        entity.setPowered(!entity.isPowered())));

        DATA_ITEMS.put(EntityType.IRON_GOLEM, new DataItem<IronGolem>((inv, entity) ->
                new ItemStackBuilder("MHF_Golem", inv.formatToggleYesNo(entity.isPlayerCreated(), "interface.iron_golem.player_created"))
                        .get(),
                (inv, event, entity) ->
                        entity.setPlayerCreated(!entity.isPlayerCreated())));

        DATA_ITEMS.put(EntityType.OCELOT, new DataItem<Ocelot>((inv, entity) ->
                new ItemStackBuilder(Material.TROPICAL_FISH, inv.formatToggleYesNo(entity.isTrusting(), "interface.ocelot.trusting"))
                        .get(),
                (inv, event, entity) ->
                        entity.setTrusting(!entity.isTrusting())));

        DATA_ITEMS.put(EntityType.PIG, new DataItem<Pig>((inv, entity) ->
                new ItemStackBuilder(Material.SADDLE, inv.formatToggleYesNo(entity.hasSaddle(), "interface.pig.saddle"))
                        .get(),
                (inv, event, entity) ->
                        entity.setSaddle(!entity.hasSaddle())));

        DATA_ITEMS.put(EntityType.RABBIT, new DataItem<Rabbit>((inv, entity) ->
                new ItemStackBuilder(Material.RABBIT_HIDE, inv.formatModify("generic.type"))
                        .enumLore(inv.translation, Rabbit.Type.values(), entity.getRabbitType(), EntityInventory::getRabbitColor, "interface.rabbit.type")
                        .get(),
                (inv, event, entity) ->
                        ItemStackBuilder.enumLoreClick(event, Rabbit.Type.values(), entity.getRabbitType(), entity::setRabbitType)));

        DATA_ITEMS.put(EntityType.SHEEP, new DataItem<Sheep>((inv, entity) ->
                new ItemStackBuilder(new ItemStack(entity.getColor() != null ? Material.getMaterial(entity.getColor().name() + "_WOOL") : Material.WHITE_WOOL), inv.formatModify("generic.color_", inv.translation.formatColor(entity.getColor())))
                        .get(),
                (inv, event, entity) ->
                        new SheepColorInventory(inv.viewer, entity, event.getView()).open()));

        DATA_ITEMS.put(EntityType.SLIME, new DataItem<Slime>((inv, entity) ->
                new ItemStackBuilder(Material.SLIME_BLOCK, inv.formatModify(EntityType.SLIME, "generic.size", entity.getSize()))
                        .get(),
                (inv, event, entity) ->
                        inv.openSign("size", entity.getSize(), 1, Integer.MAX_VALUE, Integer::parseInt, result -> {
                            if (entity.isDead()) {
                                inv.viewer.closeInventory();
                                inv.viewer.sendMessage(inv.translation.format("interface.dead"));
                                return false;
                            } else {
                                entity.setSize(result);
                                return true;
                            }
                        })));

        DATA_ITEMS.put(EntityType.SNOW_GOLEM, new DataItem<Snowman>((inv, entity) ->
                new ItemStackBuilder(Material.CARVED_PUMPKIN, inv.formatToggleYesNo(!entity.isDerp(), "interface.snow_golem.pumpkin"))
                        .get(),
                (inv, event, entity) ->
                        entity.setDerp(!entity.isDerp())));

        DATA_ITEMS.put(EntityType.VILLAGER, new DataItem<Villager>((inv, entity) -> {
            if (inv.villagerTick >= 80)
                inv.villagerTick = 0;

            return new ItemStackBuilder(++inv.villagerTick < 40 ? Main.NMS.getVillagerProfessionMaterial(entity.getProfession()) : VillagerSpecificationsInventory.getMaterialOfType(entity.getVillagerType()), inv.formatModify("interface.villager.specifications"))
                    .lore(inv.translation.format("interface.villager.profession", (inv.villagerTick < 40 ? "§l" : "") + inv.translation.format("interface.villager.profession." + entity.getProfession().getKeyOrThrow().getKey())))
                    .lore(inv.translation.format("generic.type_", (inv.villagerTick >= 40 ? "§l" : "") + inv.translation.format("interface.villager.type." + entity.getVillagerType().getKeyOrThrow().getKey()))).get();
        }, (inv, event, entity) -> {
            new VillagerSpecificationsInventory(inv.viewer, entity, event.getView()).open();
        }));

        DATA_ITEMS.put(EntityType.WOLF, new DataItem<Wolf>((inv, entity) ->
                new ItemStackBuilder(Material.BONE, inv.formatToggleYesNo(entity.isAngry(), "interface.wolf.angry"))
                        .lore("")
                        .lore(inv.translation.format("interface.wolf.collar", inv.translation.formatColor(entity.getCollarColor())))
                        .get(),
                (inv, event, entity) ->
                        entity.setAngry(!entity.isAngry())));
        //</editor-fold>

        //<editor-fold desc="Data Items - 1.13+ versions" defaultstate="collapsed">
        DATA_ITEMS.put(EntityType.PHANTOM, new DataItem<Phantom>((inv, entity) ->
                new ItemStackBuilder(Material.PHANTOM_MEMBRANE, inv.formatModify("generic.size", entity.getSize()))
                        .get(),
                (inv, event, entity) ->
                        inv.openSign("size", entity.getSize(), 0, 64, Integer::parseInt, result -> {
                            if (entity.isDead()) {
                                inv.viewer.closeInventory();
                                inv.viewer.sendMessage(inv.translation.format("interface.dead"));
                                return false;
                            } else {
                                entity.setSize(result);
                                return true;
                            }
                        })));
        //</editor-fold>

        //<editor-fold desc="Data Items - 1.14+ versions" defaultstate="collapsed">
        DATA_ITEMS.put(EntityType.CAT, new DataItem<Cat>((inv, entity) ->
                new ItemStackBuilder(Material.TROPICAL_FISH, inv.formatModify("generic.type"))
                        .registryLore(inv.translation, Registry.CAT_VARIANT, entity.getCatType(), "interface.cat.type")
                        .lore("")
                        .lore(inv.translation.format("interface.wolf.collar", inv.translation.formatColor(entity.getCollarColor())))
                        .get(),
                (inv, event, entity) ->
                        ItemStackBuilder.registryLoreClick(event, Registry.CAT_VARIANT, entity.getCatType(), entity::setCatType)));

        DATA_ITEMS.put(EntityType.FOX, new DataItem<Fox>((inv, entity) ->
                new ItemStackBuilder(entity.getFoxType() == Fox.Type.RED ? Material.SPRUCE_SAPLING : Material.SNOW_BLOCK, inv.formatToggle("generic.type"))
                        .enumLore(inv.translation, Fox.Type.values(), entity.getFoxType(), "interface.fox.type")
                        .get(),
                (inv, event, entity) ->
                        entity.setFoxType(entity.getFoxType() == Fox.Type.RED ? Fox.Type.SNOW : Fox.Type.RED)));

        DATA_ITEMS.put(EntityType.MOOSHROOM, new DataItem<MushroomCow>((inv, entity) -> {
            boolean isRed = entity.getVariant() == MushroomCow.Variant.RED;
            return new ItemStackBuilder(isRed ? Material.RED_MUSHROOM_BLOCK : Material.BROWN_MUSHROOM_BLOCK, inv.formatToggle("generic.variant"))
                    .lore((isRed ? "§2\u25ba " : "  ") + Translation.dyeColorToChat(DyeColor.RED) + "§l" + inv.translation.format("generic.color.red"))
                    .lore((!isRed ? "§2\u25ba " : "  ") + Translation.dyeColorToChat(DyeColor.BROWN) + "§l" + inv.translation.format("generic.color.brown"))
                    .get();
        }, (inv, event, entity) ->
                entity.setVariant(entity.getVariant() == MushroomCow.Variant.RED ? MushroomCow.Variant.BROWN : MushroomCow.Variant.RED)));

        DATA_ITEMS.put(EntityType.PANDA, new DataItem<Panda>((inv, entity) ->
                new ItemStackBuilder(Material.BAMBOO, inv.formatModify("interface.panda.gene"))
                        .enumLore(inv.translation, Panda.Gene.values(), entity.getMainGene(), "interface.panda.gene").get(),
                (inv, event, entity) ->
                        ItemStackBuilder.enumLoreClick(event, Panda.Gene.values(), entity.getMainGene(), entity::setMainGene)));
        //</editor-fold>

        //<editor-fold desc="Data Items - 1.16+ versions" defaultstate="collapsed">
        DATA_ITEMS.put(EntityType.STRIDER, new DataItem<Strider>((inv, entity) ->
                new ItemStackBuilder(Material.SADDLE, inv.formatToggleYesNo(entity.hasSaddle(), "interface.pig.saddle"))
                        .get(),
                (inv, event, entity) ->
                        entity.setSaddle(!entity.hasSaddle())));
        //</editor-fold>

        //<editor-fold desc="Data Items - 1.17+ versions" defaultstate="collapsed">
        DATA_ITEMS.put(EntityType.AXOLOTL, new DataItem<Axolotl>((inv, entity) ->
                new ItemStackBuilder(Material.getMaterial(getAxolotlColor(entity.getVariant()).name() + "_WOOL"), inv.formatModify("generic.variant"))
                        .enumLore(inv.translation, Axolotl.Variant.values(), entity.getVariant(), EntityInventory::getAxolotlColor, "interface.axolotl.variant")
                        .get(),
                (inv, event, entity) ->
                        ItemStackBuilder.enumLoreClick(event, Axolotl.Variant.values(), entity.getVariant(), entity::setVariant)));

        DATA_ITEMS.put(EntityType.GOAT, new DataItem<Goat>((inv, entity) ->
                new ItemStackBuilder(Material.SCULK_SENSOR, inv.formatToggleYesNo(entity.isScreaming(), "interface.goat.screaming")).get(),
                (inv, event, entity) ->
                        entity.setScreaming(!entity.isScreaming())));
        //</editor-fold>

        //<editor-fold desc="Data Items - 1.19+ versions" defaultstate="collapsed">
        DATA_ITEMS.put(EntityType.ALLAY, new DataItem<Allay>((inv, entity) -> {
            UUID memory = entity.getMemory(MemoryKey.LIKED_PLAYER);

            if (memory != null) {
                String playerName = Bukkit.getOfflinePlayer(memory).getName();
                return new ItemStackBuilder(playerName, inv.translation.format("interface.allay.owner", playerName)).get();
            } else
                return new ItemStackBuilder("MHF_Question", inv.translation.format("interface.allay.unowned")).get();
        }, null));
        //</editor-fold>

        //<editor-fold desc="Data Items - 1.21.2+ versions" defaultstate="collapsed">
        DATA_ITEMS.put(EntityType.SALMON, new DataItem<Salmon>((inv, entity) ->
                new ItemStackBuilder(Material.SALMON, inv.formatModify("generic.type"))
                        .enumLore(inv.translation, Salmon.Variant.values(), entity.getVariant(), "interface.salmon.type")
                        .get(),
                (inv, event, entity) ->
                        ItemStackBuilder.enumLoreClick(event, Salmon.Variant.values(), entity.getVariant(), entity::setVariant)));
        //</editor-fold>
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

    // 1.17+ only
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

    protected String formatModify(EntityType type, String key, Object... parameters) {
        return translation.format(key, parameters) + translation.formatCondition(Permissions.hasPermissionModify(type, viewer), "generic.modify");
    }

    protected String formatModify(String key, Object... parameters) {
        return formatModify(entity.getType(), key, parameters);
    }

    protected String formatToggleYesNo(boolean condition, String key) {
        return translation.formatYesNo(condition, key) + translation.formatCondition(Permissions.hasPermissionModify(entity.getType(), viewer), "generic.toggle");
    }

    protected String formatToggle(String key, Object... parameters) {
        return translation.format(key, parameters) + translation.formatCondition(Permissions.hasPermissionModify(entity.getType(), viewer), "generic.toggle");
    }
}