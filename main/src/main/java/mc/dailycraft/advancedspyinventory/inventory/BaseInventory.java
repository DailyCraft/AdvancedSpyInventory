package mc.dailycraft.advancedspyinventory.inventory;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.utils.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class BaseInventory {
    private static final Constructor<? extends CustomInventoryView> VIEW_CONSTRUCTOR;

    protected static final ItemStack VOID_ITEM = ItemStackBuilder.ofStainedGlassPane(DyeColor.GRAY, "").get();

    protected final Player viewer;
    protected final Translation translation;
    private final int size;

    public BaseInventory(Player viewer, int rows) {
        translation = Translation.of(this.viewer = viewer);
        size = rows * 9;
    }

    public abstract String getTitle();

    public int getSize() {
        return size;
    }

    public abstract ItemStack getItem(int index);

    public abstract void setItem(int index, ItemStack stack);

    public void onOpen(Player player) {
    }

    public void onClose(Player player) {
    }

    public abstract void onClick(InventoryClickEvent event, int rawSlot);

    public CustomInventoryView getView() {
        try {
            return VIEW_CONSTRUCTOR.newInstance(viewer, this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static ItemStack getNonNull(ItemStack privileged, ItemStack replacer) {
        return privileged != null && privileged.getType() != Material.AIR ? privileged : replacer;
    }

    protected void shift(InventoryClickEvent event, int slot, ItemStack informationItem, Predicate<Material> condition) {
        if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) && event.getCurrentItem() != null && condition.test(event.getCurrentItem().getType()) && event.getInventory().getItem(slot) != null && event.getInventory().getItem(slot).equals(informationItem)) {
            event.setCancelled(true);
            event.getInventory().setItem(slot, event.getCurrentItem());
            event.setCurrentItem(null);

            viewer.updateInventory();
        }
    }

    protected void shift(InventoryClickEvent event, int slot, EquipmentSlot equipmentSlot, Function<InformationItems, Function<Translation, ItemStack>> informationItem, String orEndsWith) {
        shift(event, slot, informationItem.apply(InformationItems.of(equipmentSlot)).apply(Translation.of((Player) event.getWhoClicked())), current -> {
            if (Main.VERSION > 16)
                return current.getEquipmentSlot() == equipmentSlot;
            else
                return (Main.VERSION > 12 ? current.getKey().getKey() : current.name().toLowerCase()).endsWith(orEndsWith);
        });
    }

    protected ItemStack getLocationItemStack(Location location, boolean isPlayer) {
        String entityKey = "interface.entity.";
        ResourceKey worldKey = Main.NMS.worldKey(location.getWorld());

        return new ItemStackBuilder(Material.ARROW, translation.format(entityKey + "location"))
                .lore(translation.format(entityKey + "world", Main.VERSION < 16 ? worldKey.getKey() : worldKey, translation.format(entityKey + "world.environment." + location.getWorld().getEnvironment().name().toLowerCase())))
                .lore(translation.format(entityKey + "x", location.getX()))
                .lore(translation.format(entityKey + "y", location.getY()))
                .lore(translation.format(entityKey + "z", location.getZ()))
                .lore(translation.format(entityKey + "yaw", location.getYaw()))
                .lore(translation.format(entityKey + "pitch", location.getPitch()))
                .lore(isPlayer ? Permissions.PLAYER_TELEPORT.has(viewer) || Permissions.PLAYER_TELEPORT_OTHERS.has(viewer) : Permissions.ENTITY_TELEPORT.has(viewer) || Permissions.ENTITY_TELEPORT_OTHERS.has(viewer), "")
                .lore((isPlayer ? Permissions.PLAYER_TELEPORT : Permissions.ENTITY_TELEPORT).has(viewer), translation.format(entityKey + "teleport"))
                .lore((isPlayer ? Permissions.PLAYER_TELEPORT_OTHERS : Permissions.ENTITY_TELEPORT_OTHERS).has(viewer), translation.format(entityKey + "teleport.to_me")).get();
    }

    protected void replaceItem(InventoryClickEvent event, ItemStack informationItem) {
        if (event.getCurrentItem() != null && event.getCurrentItem().equals(informationItem) && (event.getCursor() == null || event.getCursor().getType() == Material.AIR) || (event.getCurrentItem().equals(informationItem) && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(false);

        if (event.getCurrentItem() != null && event.getCurrentItem().equals(informationItem))
            event.setCurrentItem(null);

        if (event.getCursor().equals(informationItem))
            event.getView().setCursor(null);

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            if (event.getCurrentItem() != null && event.getCurrentItem().equals(informationItem))
                event.setCurrentItem(null);

            if (event.getCursor().equals(informationItem))
                event.getView().setCursor(null);

            viewer.updateInventory();
        });
    }

    protected <T extends Number> void openSign(String formatKey, T defaultValue, T minimumValue, T maximumValue, Function<String, T> stringToT, Predicate<T> runAfter) {
        Location loc = viewer.getLocation().clone();
        loc.setY(Math.max(0, loc.getY() - 5));

        viewer.closeInventory();

        if (Main.VERSION > 13)
            viewer.sendBlockChange(loc, Material.OAK_SIGN.createBlockData());
        else if (Main.VERSION > 12)
            viewer.sendBlockChange(loc, Material.getMaterial("SIGN").createBlockData());
        else
            viewer.sendBlockChange(loc, Material.getMaterial("SIGN_POST"), (byte) 0);

        viewer.sendSignChange(loc, new String[] {defaultValue.toString(), "^^^^^^^^^^^^^^^", translation.format("sign." + formatKey + ".0"), translation.format("sign." + formatKey + ".1")});

        Triplet<Object> triplet = (Triplet<Object>) Main.NMS.openSign(viewer, loc);
        String handlerId = Main.getInstance().getName().toLowerCase() + "_sign_" + new Random().nextLong();

        triplet.pipeline.addBefore("packet_handler", handlerId, new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (triplet.packet.isInstance(msg)) {
                    String line = triplet.line.apply(msg);
                    T result;

                    try {
                        T converted = line.isEmpty() ? defaultValue : stringToT.apply(line);

                        if (converted.doubleValue() < minimumValue.doubleValue())
                            result = minimumValue;
                        else if (converted.doubleValue() > maximumValue.doubleValue())
                            result = maximumValue;
                        else
                            result = converted;
                    } catch (NumberFormatException exception) {
                        result = defaultValue;
                    }

                    if (Main.VERSION > 12)
                        viewer.sendBlockChange(loc, loc.getBlock().getBlockData());
                    else
                        viewer.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());

                    final T finalResult = result;
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        if (runAfter.test(finalResult))
                            getView().open();
                    });

                    triplet.pipeline.channel().eventLoop().submit(() -> triplet.pipeline.remove(handlerId));
                } else
                    super.channelRead(ctx, msg);
            }
        });
    }

    protected <T extends Number> void openSign(String formatKey, T defaultValue, T minimumValue, T maximumValue, Function<String, T> stringToT, Consumer<T> runAfter) {
        openSign(formatKey, defaultValue, minimumValue, maximumValue, stringToT, t -> {
            runAfter.accept(t);
            return true;
        });
    }

    static {
        try {
            VIEW_CONSTRUCTOR = (Constructor<CustomInventoryView>) Class.forName("mc.dailycraft.advancedspyinventory.utils.CustomInventoryView" + (Main.VERSION <= 13 ? "Old" : "New"))
                    .getConstructor(Player.class, BaseInventory.class);
        } catch (NoSuchMethodException | ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }
}