package mc.dailycraft.advancedspyinventory.inventory;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.ItemStackBuilder;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import mc.dailycraft.advancedspyinventory.utils.Translation;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractInventory implements IInventory {
    protected static final ItemStack VOID_ITEM = new ItemStackBuilder(Material.GRAY_STAINED_GLASS_PANE, "").nms();

    private final List<HumanEntity> viewers = new ArrayList<>();

    protected final Player viewer;
    protected final Translation translation;
    private final int size;

    public AbstractInventory(Player viewer, int rows) {
        this.viewer = viewer;
        translation = Translation.of(viewer);
        size = rows * 9;
    }

    @Override
    public abstract List<ItemStack> getContents();

    @Override
    public abstract ItemStack getItem(int index);

    @Override
    public abstract void setItem(int index, ItemStack stack);

    public abstract String getTitle();

    public abstract void onClick(InventoryClickEvent event, int rawSlot);

    public CustomInventoryView getView() {
        return new CustomInventoryView(viewer, this);
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        viewers.add(who);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        viewers.remove(who);
    }

    // UTILITY METHODS //

    public static ItemStack getNonNull(ItemStack privileged, ItemStack replacer) {
        return privileged != null && !privileged.isEmpty() ? privileged : replacer;
    }

    protected void shift(InventoryClickEvent event, int slot, org.bukkit.inventory.ItemStack informationItem, Predicate<ItemStack> condition) {
        if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) && condition.test(CraftItemStack.asNMSCopy(event.getCurrentItem())) && event.getInventory().getItem(slot).equals(informationItem)) {
            event.setCancelled(true);
            event.getInventory().setItem(slot, event.getCurrentItem());
            event.setCurrentItem(null);

            viewer.updateInventory();
        }
    }

    protected ItemStack getLocationItemStack(Location location, boolean isPlayer) {
        String entityKey = "interface.entity.";

        return new ItemStackBuilder(Material.ARROW, translation.format(entityKey + "location"))
                .lore(translation.format(entityKey + "world", location.getWorld().getName(), translation.format(entityKey + "world.environment." + location.getWorld().getEnvironment().name().toLowerCase())))
                .lore(translation.format(entityKey + "x", location.getX()))
                .lore(translation.format(entityKey + "y", location.getY()))
                .lore(translation.format(entityKey + "z", location.getZ()))
                .lore(translation.format(entityKey + "yaw", location.getYaw()))
                .lore(translation.format(entityKey + "pitch", location.getPitch()))
                .lore(isPlayer ? Permissions.PLAYER_TELEPORT.has(viewer) : Permissions.ENTITY_TELEPORT.has(viewer), "", translation.format(entityKey + "teleport")).nms();
    }

    protected void replaceItem(InventoryClickEvent event, org.bukkit.inventory.ItemStack informationItem) {
        if (event.getCurrentItem().equals(informationItem) && (event.getCursor() == null || event.getCursor().getType() == Material.AIR)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(false);

        if (event.getCurrentItem().equals(informationItem))
            event.setCurrentItem(null);

        if (event.getCursor().equals(informationItem))
            event.getView().setCursor(null);

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            if (event.getCurrentItem().equals(informationItem))
                event.setCurrentItem(null);

            if (event.getCursor().equals(informationItem))
                event.getView().setCursor(null);

            ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(new PacketPlayOutSetSlot(((CraftPlayer) viewer).getHandle().activeContainer.windowId, event.getRawSlot(), CraftItemStack.asNMSCopy(event.getCurrentItem())));
        });

    }

    protected <T extends Number> void signInterface(CustomInventoryView view, String formatKey, T defaultValue, T minimumValue, T maximumValue, Function<String, T> stringToT, Consumer<T> runAfter) {
        EntityPlayer nmsViewer = ((CraftPlayer) viewer).getHandle();
        BlockPosition position = new BlockPosition(nmsViewer.getPositionVector().add(0, -nmsViewer.getPositionVector().getY(), 0));

        TileEntitySign teSign = new TileEntitySign();
        teSign.setPosition(position);

        teSign.a(0, new ChatComponentText(defaultValue.toString()));
        teSign.a(1, new ChatComponentText("^^^^^^^^^^^^^^^"));
        teSign.a(2, new ChatComponentText(translation.format("sign." + formatKey + ".0")));
        teSign.a(3, new ChatComponentText(translation.format("sign." + formatKey + ".1")));

        viewer.closeInventory();

        nmsViewer.playerConnection.sendPacket(new PacketPlayOutBlockChange(position, ((CraftBlockData) Material.OAK_SIGN.createBlockData()).getState()));
        nmsViewer.playerConnection.sendPacket(teSign.getUpdatePacket());
        nmsViewer.playerConnection.sendPacket(new PacketPlayOutOpenSignEditor(position));

        ChannelPipeline pipeline = nmsViewer.playerConnection.networkManager.channel.pipeline();

        String handlerId = mc.dailycraft.advancedspyinventory.Main.getInstance().getName().toLowerCase() + "_sign_" + new Random().nextLong();

        pipeline.addBefore("packet_handler", handlerId, new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext context, Object msg) {
                if (msg instanceof PacketPlayInUpdateSign) {
                    T result;

                    try {
                        String line = ((PacketPlayInUpdateSign) msg).c()[0];
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

                    nmsViewer.playerConnection.sendPacket(new PacketPlayOutBlockChange(position, ((CraftBlockData) viewer.getLocation().getBlock().getBlockData()).getState()));
                    pipeline.channel().eventLoop().submit(() -> pipeline.remove(handlerId));

                    final T finalResult = result;
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        runAfter.accept(finalResult);
                        view.open();
                    });

                    return;
                }

                context.fireChannelRead(msg);
            }
        });
    }

    // USELESS METHODS //

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public ItemStack splitStack(int index, int amount) {
        ItemStack stack = getItem(index);

        if (stack == ItemStack.b)
            return stack;
        else {
            ItemStack result;

            if (stack.getCount() <= amount) {
                setItem(index, ItemStack.b);
                result = stack;
            } else {
                result = CraftItemStack.copyNMSStack(stack, amount);
                stack.subtract(amount);
                setItem(index, stack);
            }

            return result;
        }
    }

    @Override
    public ItemStack splitWithoutUpdate(int index) {
        ItemStack stack = getItem(index);

        if (stack.isEmpty())
            return ItemStack.b;
        else {
            setItem(index, ItemStack.b);
            return stack;
        }
    }

    @Override
    public List<HumanEntity> getViewers() {
        return viewers;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void update() {
    }

    @Override
    public boolean a(EntityHuman human) {
        return true;
    }

    @Override
    public InventoryHolder getOwner() {
        return null;
    }

    @Override
    public void setMaxStackSize(int size) {
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void clear() {
    }
}