package mc.dailycraft.advancedspyinventory.nms.v1_19_R1;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.Translation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;

import java.util.Iterator;
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
        ServerPlayer nmsViewer = ((CraftPlayer) view.getPlayer()).getHandle();
        Translation translation = Translation.of(view.getPlayer());
        BlockPos position = new BlockPos(nmsViewer.position().add(0, -nmsViewer.position().y, 0));

        SignBlockEntity teSign = new SignBlockEntity(position, ((CraftBlockData) Material.OAK_SIGN.createBlockData()).getState());

        teSign.setMessage(0, Component.literal(defaultValue.toString()));
        teSign.setMessage(1, Component.literal("^".repeat(15)));
        teSign.setMessage(2, Component.literal(translation.format("sign." + formatKey + ".0")));
        teSign.setMessage(3, Component.literal(translation.format("sign." + formatKey + ".1")));

        view.getPlayer().closeInventory();

        nmsViewer.connection.send(new ClientboundBlockUpdatePacket(position, ((CraftBlockData) Material.OAK_SIGN.createBlockData()).getState()));
        nmsViewer.connection.send(teSign.getUpdatePacket());
        nmsViewer.connection.send(new ClientboundOpenSignEditorPacket(position));

        ChannelPipeline pipeline = nmsViewer.connection.connection.channel.pipeline();

        String handlerId = Main.getInstance().getName().toLowerCase() + "_sign_" + new Random().nextLong();

        pipeline.addBefore("packet_handler", handlerId, new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext context, Object msg) {
                if (msg instanceof ServerboundSignUpdatePacket) {
                    T result;

                    try {
                        String line = ((ServerboundSignUpdatePacket) msg).getLines()[0];
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

                    nmsViewer.connection.send(new ClientboundBlockUpdatePacket(position, ((CraftBlockData) view.getPlayer().getLocation().getBlock().getBlockData()).getState()));
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
    public Material getVillagerProfessionMaterial(Villager.Profession profession) {
        return switch (profession) {
            case NONE ->
                    Material.BELL;
            case NITWIT ->
                    Material.OAK_DOOR;
            default -> {
                for (Holder.Reference<PoiType> holder : Registry.POINT_OF_INTEREST_TYPE.holders().toList()) {
                    if (CraftVillager.bukkitToNmsProfession(profession).acquirableJobSite().test(holder)) {
                        Iterator<BlockState> iterator = holder.value().matchingStates().iterator();
                        yield iterator.hasNext() ? CraftMagicNumbers.getMaterial(iterator.next().getBlock().asItem()) : Material.RED_BED;
                    }
                }

                yield Material.RED_BED;
            }
        };
    }
}