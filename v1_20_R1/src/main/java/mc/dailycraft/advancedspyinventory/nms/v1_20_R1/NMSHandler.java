package mc.dailycraft.advancedspyinventory.nms.v1_20_R1;

import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.Triplet;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftLocation;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftMagicNumbers;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.UUID;

public class NMSHandler implements mc.dailycraft.advancedspyinventory.nms.NMSHandler {
    private static Field connectionField;

    @Override
    public NMSData getData(UUID playerUuid) {
        return new NMSData(playerUuid);
    }

    @Override
    public ItemStack getEquipment(LivingEntity entity, EquipmentSlot slot) {
        return CraftItemStack.asCraftMirror(((CraftLivingEntity) entity).getHandle().getItemBySlot(CraftEquipmentSlot.getNMS(slot)));
    }

    @Override
    public Inventory createInventory(BaseInventory inventory) {
        return new CraftInventory(new NMSContainer(inventory));
    }

    @Override
    public Triplet<?> openSign(Player player, Location loc) {
        if (connectionField == null) {
            try {
                (connectionField = ServerGamePacketListenerImpl.class.getDeclaredField("h")).setAccessible(true);
            } catch (NoSuchFieldException exception) {
                throw new RuntimeException(exception);
            }
        }

        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        connection.send(new ClientboundOpenSignEditorPacket(CraftLocation.toBlockPosition(loc), true));
        try {
            return new Triplet<>(((Connection) connectionField.get(connection)).channel.pipeline(), ServerboundSignUpdatePacket.class, packet -> packet.getLines()[0]);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Material getVillagerProfessionMaterial(Villager.Profession profession) {
        return switch (profession) {
            case NONE ->
                    Material.BELL;
            case NITWIT ->
                    Material.OAK_DOOR;
            default -> {
                for (Holder.Reference<PoiType> holder : BuiltInRegistries.POINT_OF_INTEREST_TYPE.holders().toList()) {
                    if (CraftVillager.bukkitToNmsProfession(profession).acquirableJobSite().test(holder)) {
                        Iterator<BlockState> iterator = holder.value().matchingStates().iterator();
                        yield iterator.hasNext() ? CraftMagicNumbers.getMaterial(iterator.next().getBlock().asItem()) : Material.RED_BED;
                    }
                }

                yield Material.RED_BED;
            }
        };
    }

    @Override
    public void setBasePotionType(PotionMeta meta, PotionType potionType) {
        meta.setBasePotionData(new PotionData(potionType));
    }
}