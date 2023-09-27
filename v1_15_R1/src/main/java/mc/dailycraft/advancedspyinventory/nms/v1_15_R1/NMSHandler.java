package mc.dailycraft.advancedspyinventory.nms.v1_15_R1;

import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.Triplet;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftOcelot;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_15_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftMagicNumbers;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
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
                    // 1.15.2
                    Method method = CraftContainer.class.getMethod("getNotchInventoryType", Inventory.class);
                    windowType = (Containers<?>) method.invoke(null, view.getTopInventory());
                } catch (NoSuchMethodException exception) {
                    // 1.15 / 1.15.1
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
                        throw new RuntimeException(exception1);
                    }
                } catch (InvocationTargetException | IllegalAccessException exception) {
                    throw new RuntimeException(exception);
                }

                String title = view.getTitle();
                nmsPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, windowType, CraftChatMessage.fromString(title)[0]));
                nmsPlayer.activeContainer = container;
                nmsPlayer.activeContainer.addSlotListener(nmsPlayer);
            }
        }
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
                (matchingStatesField = VillagePlaceType.class.getDeclaredField("z")).setAccessible(true);
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

    @Override
    public void dropItem(Player player, boolean dropAll) {
        ((CraftPlayer) player).getHandle().n(dropAll);
    }
}