package mc.dailycraft.advancedspyinventory.nms.v1_13_R2;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.Translation;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

public class NMSHandler implements mc.dailycraft.advancedspyinventory.nms.NMSHandler {
    @Override
    public NMSData getData(UUID playerUuid) {
        return new NMSData(playerUuid);
    }

    @Override
    public Inventory createInventory(BaseInventory inventory) {
        return new CraftInventory(new NMSContainer(inventory));
    }

    @Override
    public <T extends Number> void signInterface(CustomInventoryView view, String formatKey, T defaultValue, T minimumValue, T maximumValue, Function<String, T> stringToT, Predicate<T> runAfter) {
        EntityPlayer nmsViewer = ((CraftPlayer) view.getPlayer()).getHandle();
        Translation translation = Translation.of(view.getPlayer());
        BlockPosition position = new BlockPosition(nmsViewer.bI().add(0, -nmsViewer.bI().y, 0));

        TileEntitySign teSign = new TileEntitySign();
        teSign.setPosition(position);

        teSign.a(0, new ChatComponentText(defaultValue.toString()));
        teSign.a(1, new ChatComponentText("^^^^^^^^^^^^^^^"));
        teSign.a(2, new ChatComponentText(translation.format("sign." + formatKey + ".0")));
        teSign.a(3, new ChatComponentText(translation.format("sign." + formatKey + ".1")));

        view.getPlayer().closeInventory();

        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(nmsViewer.world, position);
        packet.block = ((CraftBlockData) Material.SIGN.createBlockData()).getState();
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

    @Override
    public String[] getVillagerProfessions() {
        return Arrays.stream(Villager.Career.values()).map(Enum::name).toArray(String[]::new);
    }

    @Override
    public String getVillagerProfession(Villager villager) {
        return villager.getCareer().name();
    }

    @Override
    public void setVillagerProfession(Villager villager, String profession) {
        Villager.Career career = Villager.Career.valueOf(profession);
        villager.setProfession(career.getProfession());
        do {
            villager.setCareer(career);
        } while (villager.getCareer() != career);
    }

    @Override
    public void dropItem(Player player, boolean dropAll) {
        ((CraftPlayer) player).getHandle().a(dropAll);
    }
}