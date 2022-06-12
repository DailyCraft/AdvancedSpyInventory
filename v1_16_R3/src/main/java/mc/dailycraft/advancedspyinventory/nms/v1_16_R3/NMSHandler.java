package mc.dailycraft.advancedspyinventory.nms.v1_16_R3;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.Translation;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class NMSHandler implements mc.dailycraft.advancedspyinventory.nms.NMSHandler {
    private static Field matchingStatesField = null;

    @Override
    public String worldId(World world) {
        return ((CraftWorld) world).getHandle().getDimensionKey().a().toString();
    }

    @Override
    public NMSData getData(UUID playerUuid) {
        return new NMSData(playerUuid);
    }

    @Override
    public Inventory containerToInventory(mc.dailycraft.advancedspyinventory.nms.NMSContainer container) {
        return new CraftInventory(new NMSContainer(container));
    }

    @Override
    public <T extends Number> void signInterface(CustomInventoryView view, String formatKey, T defaultValue, T minimumValue, T maximumValue, Function<String, T> stringToT, Consumer<T> runAfter) {
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

        nmsViewer.playerConnection.sendPacket(new PacketPlayOutBlockChange(position, ((CraftBlockData) Material.OAK_SIGN.createBlockData()).getState()));
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

                    nmsViewer.playerConnection.sendPacket(new PacketPlayOutBlockChange(position, ((CraftBlockData) view.getPlayer().getLocation().getBlock().getBlockData()).getState()));
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

    @Override
    public Material getVillagerProfessionMaterial(Villager.Profession profession) {
        if (matchingStatesField == null) {
            try {
                matchingStatesField = VillagePlaceType.class.getDeclaredField("C");
                matchingStatesField.setAccessible(true);
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
}