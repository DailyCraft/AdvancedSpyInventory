package mc.dailycraft.advancedspyinventory.nms.v1_18_R2;

import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.ResourceKey;
import mc.dailycraft.advancedspyinventory.utils.Triplet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;
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
import java.util.Set;
import java.util.UUID;

public class NMSHandler implements mc.dailycraft.advancedspyinventory.nms.NMSHandler {
    private static Field matchingStatesField = null;

    @Override
    public ResourceKey worldKey(World world) {
        return ResourceKey.fromOther(((CraftWorld) world).getHandle().dimension().location(),
                ResourceLocation::getNamespace, ResourceLocation::getPath);
    }

    @Override
    public ItemStack getEquipment(LivingEntity entity, EquipmentSlot slot) {
        return CraftItemStack.asCraftMirror(((CraftLivingEntity) entity).getHandle().getItemBySlot(CraftEquipmentSlot.getNMS(slot)));
    }

    @Override
    public NMSData getData(UUID playerUuid) {
        return new NMSData(playerUuid);
    }

    @Override
    public Inventory createInventory(BaseInventory inventory) {
        return new CraftInventory(new NMSContainer(inventory));
    }

    @Override
    public Triplet<?> openSign(Player player, Location loc) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        connection.send(new ClientboundOpenSignEditorPacket(new BlockPos(loc.getX(), loc.getY(), loc.getZ())));
        return new Triplet<>(connection.connection.channel.pipeline(), ServerboundSignUpdatePacket.class, packet -> packet.getLines()[0]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Material getVillagerProfessionMaterial(Villager.Profession profession) {
        if (matchingStatesField == null) {
            try {
                (matchingStatesField = PoiType.class.getDeclaredField("E")).setAccessible(true);
            } catch (NoSuchFieldException exception) {
                throw new RuntimeException(exception);
            }
        }

        return switch (profession) {
            case NONE ->
                    Material.BELL;
            case NITWIT ->
                    Material.OAK_DOOR;
            default -> {
                try {
                    Iterator<BlockState> matchingStates = ((Set<BlockState>) matchingStatesField.get(CraftVillager.bukkitToNmsProfession(profession).getJobPoiType())).iterator();
                    yield matchingStates.hasNext() ? CraftMagicNumbers.getMaterial(matchingStates.next().getBlock()) : Material.RED_BED;
                } catch (IllegalAccessException exception) {
                    throw new RuntimeException(exception);
                }
            }
        };
    }

    @Override
    public void setBasePotionType(PotionMeta meta, PotionType potionType) {
        meta.setBasePotionData(new PotionData(potionType));
    }
}