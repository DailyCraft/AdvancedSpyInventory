package mc.dailycraft.advancedspyinventory.nms.v1_15_R1;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.Translation;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftOcelot;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_15_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

public class NMSHandler implements mc.dailycraft.advancedspyinventory.nms.NMSHandler {
    private static Field matchingStatesField = null;
    private static Method isTrustingMethod = null, setTrustingMethod = null;

    @Override
    public NMSData getData(UUID playerUuid) {
        return new NMSData(playerUuid);
    }

    @Override
    public Inventory createInventory(BaseInventory inventory) {
        return new CraftInventory(new NMSContainer(inventory));
    }

    @Override
    public void openInventory(Player player, InventoryView view) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        if (nmsPlayer.playerConnection != null) {
            if (nmsPlayer.activeContainer != nmsPlayer.defaultContainer)
                nmsPlayer.playerConnection.a(new PacketPlayInCloseWindow(nmsPlayer.activeContainer.windowId));

            Container container = new CraftContainer(view, nmsPlayer, nmsPlayer.nextContainerCounter());
            container = CraftEventFactory.callInventoryOpenEvent(nmsPlayer, container);
            if (container != null) {
                Containers<?> windowType = null;

                try {
                    // 1.18.2
                    Method method = CraftContainer.class.getMethod("getNotchInventoryType", Inventory.class);
                    windowType = (Containers<?>) method.invoke(null, view.getTopInventory());
                } catch (NoSuchMethodException exception) {
                    // 1.18 / 1.18.1
                    try {
                        if (view.getType() != InventoryType.CHEST) {
                            Method method = CraftContainer.class.getMethod("getNotchInventoryType", InventoryType.class);
                            windowType = (Containers<?>) method.invoke(null, view.getType());
                        } else {
                            switch (view.getTopInventory().getSize()) {
                                case 9:
                                    windowType = Containers.GENERIC_9X1;
                                    break;
                                case 9 * 2:
                                    windowType = Containers.GENERIC_9X2;
                                    break;
                                case 9 * 3:
                                    windowType = Containers.GENERIC_9X3;
                                    break;
                                case 9 * 4:
                                    windowType = Containers.GENERIC_9X4;
                                    break;
                                case 9 * 5:
                                    windowType = Containers.GENERIC_9X5;
                                    break;
                                case 9 * 6:
                                    windowType = Containers.GENERIC_9X6;
                                    break;
                            }
                        }
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException exception1) {
                        exception1.printStackTrace();
                    }
                } catch (InvocationTargetException | IllegalAccessException exception) {
                    exception.printStackTrace();
                }

                String title = view.getTitle();
                nmsPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, windowType, CraftChatMessage.fromString(title)[0]));
                nmsPlayer.activeContainer = container;
                nmsPlayer.activeContainer.addSlotListener(nmsPlayer);
            }
        }
    }

    @Override
    public <T extends Number> void signInterface(CustomInventoryView view, String formatKey, T defaultValue, T minimumValue, T maximumValue, Function<String, T> stringToT, Predicate<T> runAfter) {
        EntityPlayer nmsViewer = ((CraftPlayer) view.getPlayer()).getHandle();
        Translation translation = Translation.of(view.getPlayer());
        BlockPosition position = new BlockPosition(nmsViewer.getPositionVector().add(0, -nmsViewer.getPositionVector().y, 0));

        TileEntitySign teSign = new TileEntitySign();
        teSign.setPosition(position);

        teSign.a(0, new ChatComponentText(defaultValue.toString()));
        teSign.a(1, new ChatComponentText("^^^^^^^^^^^^^^^"));
        teSign.a(2, new ChatComponentText(translation.format("sign." + formatKey + ".0")));
        teSign.a(3, new ChatComponentText(translation.format("sign." + formatKey + ".1")));

        view.getPlayer().closeInventory();

        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(nmsViewer.world, position);
        packet.block = ((CraftBlockData) Material.OAK_SIGN.createBlockData()).getState();
        nmsViewer.playerConnection.sendPacket(packet);
        nmsViewer.playerConnection.sendPacket(teSign.getUpdatePacket());
        nmsViewer.playerConnection.sendPacket(new PacketPlayOutOpenSignEditor(position));

        ChannelPipeline pipeline = nmsViewer.playerConnection.networkManager.channel.pipeline();

        String handlerId = Main.getInstance().getName().toLowerCase() + "_sign_" + new Random().nextLong();

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

                    PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(nmsViewer.world, position);
                    packet.block = ((CraftBlockData) view.getPlayer().getLocation().getBlock().getBlockData()).getState();
                    nmsViewer.playerConnection.sendPacket(packet);
                    pipeline.channel().eventLoop().submit(() -> pipeline.remove(handlerId));

                    final T finalResult = result;
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        if (runAfter.test(finalResult))
                            view.open();
                    });

                    return;
                }

                context.fireChannelRead(msg);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public Material getVillagerProfessionMaterial(Villager.Profession profession) {
        if (matchingStatesField == null) {
            try {
                (matchingStatesField = VillagePlaceType.class.getDeclaredField("z")).setAccessible(true);
            } catch (NoSuchFieldException exception) {
                exception.printStackTrace();
            }
        }

        switch (profession) {
            case NONE:
                return Material.BELL;
            case NITWIT:
                return Material.OAK_DOOR;
            default:
                try {
                    Iterator<IBlockData> matchingStates = ((Set<IBlockData>) matchingStatesField.get(CraftVillager.bukkitToNmsProfession(profession).b())).iterator();
                    return matchingStates.hasNext() ? CraftMagicNumbers.getMaterial(matchingStates.next().getBlock()) : Material.RED_BED;
                } catch (IllegalAccessException exception) {
                    throw new RuntimeException(exception);
                }
        }
    }

    @Override
    public boolean isOcelotTrusting(Ocelot ocelot) {
        if (isTrustingMethod == null) {
            try {
                isTrustingMethod = EntityOcelot.class.getDeclaredMethod("isTrusting");
                isTrustingMethod.setAccessible(true);
            } catch (NoSuchMethodException exception) {
                exception.printStackTrace();
            }
        }

        try {
            return (boolean) isTrustingMethod.invoke(((CraftOcelot) ocelot).getHandle());
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void setOcelotTrusting(Ocelot ocelot, boolean trusting) {
        if (setTrustingMethod == null) {
            try {
                setTrustingMethod = EntityOcelot.class.getDeclaredMethod("setTrusting", boolean.class);
                setTrustingMethod.setAccessible(true);
            } catch (NoSuchMethodException exception) {
                exception.printStackTrace();
            }
        }

        try {
            setTrustingMethod.invoke(((CraftOcelot) ocelot).getHandle(), trusting);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void dropItem(Player player, boolean dropAll) {
        ((CraftPlayer) player).getHandle().n(dropAll);
    }
}