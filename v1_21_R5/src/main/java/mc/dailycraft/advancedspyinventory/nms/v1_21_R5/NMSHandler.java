package mc.dailycraft.advancedspyinventory.nms.v1_21_R5;

import com.mojang.authlib.GameProfile;
import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.Triplet;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R5.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftLocation;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftMagicNumbers;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

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
    public InventoryView createView(Player viewer, BaseInventory inventory) {
        return new CustomInventoryView(viewer, inventory);
    }

    @Override
    public Triplet<?> openSign(Player player, Location loc) {
        if (connectionField == null) {
            try {
                (connectionField = ServerCommonPacketListenerImpl.class.getDeclaredField("e")).setAccessible(true);
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
        if (profession == Villager.Profession.NONE)
            return Material.BELL;
        else if (profession == Villager.Profession.NITWIT)
            return Material.OAK_DOOR;

        for (Holder<PoiType> holder : BuiltInRegistries.POINT_OF_INTEREST_TYPE.asHolderIdMap()) {
            if (CraftVillager.CraftProfession.bukkitToMinecraft(profession).acquirableJobSite().test(holder)) {
                Iterator<BlockState> iterator = holder.value().matchingStates().iterator();
                return iterator.hasNext() ? CraftMagicNumbers.getMaterial(iterator.next().getBlock().asItem()) : Material.RED_BED;
            }
        }

        return Material.RED_BED;
    }

    @Override
    public void setHeadProfile(SkullMeta meta, GameProfile profile) throws ReflectiveOperationException {
        Variables.setProfileField(meta, new ResolvableProfile(profile));
    }
}