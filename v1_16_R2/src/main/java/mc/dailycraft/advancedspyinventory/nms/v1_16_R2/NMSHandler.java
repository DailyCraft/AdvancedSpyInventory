package mc.dailycraft.advancedspyinventory.nms.v1_16_R2;

import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.ResourceKey;
import mc.dailycraft.advancedspyinventory.utils.Triplet;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R2.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftOcelot;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R2.util.CraftMagicNumbers;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class NMSHandler implements mc.dailycraft.advancedspyinventory.nms.NMSHandler {
    private static Field matchingStatesField = null;
    private static Method isTrustingMethod = null, setTrustingMethod = null;

    @Override
    public ResourceKey worldKey(World world) {
        return ResourceKey.fromOther(((CraftWorld) world).getHandle().getDimensionKey().a(),
                MinecraftKey::getNamespace, MinecraftKey::getKey);
    }

    @Override
    public ItemStack getEquipment(LivingEntity entity, EquipmentSlot slot) {
        return CraftItemStack.asCraftMirror(((CraftLivingEntity) entity).getHandle().getEquipment(CraftEquipmentSlot.getNMS(slot)));
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
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutOpenSignEditor(new BlockPosition(loc.getX(), loc.getY(), loc.getZ())));
        return new Triplet<>(connection.networkManager.channel.pipeline(), PacketPlayInUpdateSign.class, packet -> packet.c()[0]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Material getVillagerProfessionMaterial(Villager.Profession profession) {
        if (matchingStatesField == null) {
            try {
                (matchingStatesField = VillagePlaceType.class.getDeclaredField("C")).setAccessible(true);
            } catch (NoSuchFieldException exception) {
                throw new RuntimeException(exception);
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
                (isTrustingMethod = EntityOcelot.class.getDeclaredMethod("isTrusting")).setAccessible(true);
            } catch (NoSuchMethodException exception) {
                throw new RuntimeException(exception);
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
                (setTrustingMethod = EntityOcelot.class.getDeclaredMethod("setTrusting", boolean.class)).setAccessible(true);
            } catch (NoSuchMethodException exception) {
                throw new RuntimeException(exception);
            }
        }

        try {
            setTrustingMethod.invoke(((CraftOcelot) ocelot).getHandle(), trusting);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }
}