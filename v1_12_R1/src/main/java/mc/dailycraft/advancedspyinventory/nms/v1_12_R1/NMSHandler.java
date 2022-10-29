package mc.dailycraft.advancedspyinventory.nms.v1_12_R1;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.Triplet;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class NMSHandler implements mc.dailycraft.advancedspyinventory.nms.NMSHandler {
    private static Field careerLevelField, careerField, tradesField;
    private static Method populateTradesMethod;

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
        return new Triplet<>(connection.networkManager.channel.pipeline(), PacketPlayInUpdateSign.class, packet -> packet.b()[0]);
    }

    @Override
    public String[] getVillagerProfessions() {
        return Arrays.stream(Career.values()).map(Enum::name).toArray(String[]::new);
    }

    @Override
    public String getVillagerProfession(Villager villager) {
        return getVillagerCareer(villager).name();
    }

    @Override
    public void setVillagerProfession(Villager villager, String profession) {
        try {
            if (careerLevelField == null)
                (careerLevelField = EntityVillager.class.getDeclaredField("bL")).setAccessible(true);
            if (tradesField == null)
                (tradesField = EntityVillager.class.getDeclaredField("trades")).setAccessible(true);
            if (populateTradesMethod == null)
                (populateTradesMethod = EntityVillager.class.getDeclaredMethod("dx")).setAccessible(true);
        } catch (NoSuchFieldException | NoSuchMethodException exception) {
            exception.printStackTrace();
        }

        Career career = Career.valueOf(profession);
        villager.setProfession(career.getProfession());
        setVillagerCareer(villager, career);

        EntityVillager handle = ((CraftVillager) villager).getHandle();

        do
        {
            try {
                tradesField.set(handle, null);
                careerLevelField.set(handle, 0);
                populateTradesMethod.invoke(handle);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        } while (getVillagerCareer(villager) != career);
    }

    @Override
    public void dropItem(Player player, boolean dropAll) {
        ((CraftPlayer) player).getHandle().a(dropAll);
    }

    // Replicate CraftBukkit 1.12.2 functions not implemented for 1.12.1 and 1.12 versions.

    private static final Map<Career, Integer> careerIDMap = new HashMap<>();

    static {
        int id = 0;

        for (Villager.Profession profession : Villager.Profession.values()) {
            for (Career c : Career.getCareers(profession))
                careerIDMap.put(c, ++id);

            id = 0;
        }

    }

    private static Career getVillagerCareer(Villager villager) {
        if (careerField == null) {
            try {
                (careerField = EntityVillager.class.getDeclaredField("bK")).setAccessible(true);
            } catch (NoSuchFieldException exception) {
                exception.printStackTrace();
            }
        }

        try {
            int villagerCareer = (int) careerField.get(((CraftVillager) villager).getHandle());

            for (Career career : Career.getCareers(villager.getProfession()))
                if (careerIDMap.containsKey(career) && careerIDMap.get(career) == villagerCareer)
                    return career;
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    private static void setVillagerCareer(Villager villager, Career career) {
        if (careerField == null) {
            try {
                (careerField = EntityVillager.class.getDeclaredField("bK")).setAccessible(true);
            } catch (NoSuchFieldException exception) {
                exception.printStackTrace();
            }
        }

        try {
            careerField.set(((CraftVillager) villager).getHandle(), career == null ? 0 : careerIDMap.getOrDefault(career, 0));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public enum Career {
        FARMER(Villager.Profession.FARMER),
        FISHERMAN(Villager.Profession.FARMER),
        SHEPHERD(Villager.Profession.FARMER),
        FLETCHER(Villager.Profession.FARMER),
        LIBRARIAN(Villager.Profession.LIBRARIAN),
        CARTOGRAPHER(Villager.Profession.LIBRARIAN),
        CLERIC(Villager.Profession.PRIEST),
        ARMORER(Villager.Profession.BLACKSMITH),
        WEAPON_SMITH(Villager.Profession.BLACKSMITH),
        TOOL_SMITH(Villager.Profession.BLACKSMITH),
        BUTCHER(Villager.Profession.BUTCHER),
        LEATHERWORKER(Villager.Profession.BUTCHER),
        NITWIT(Villager.Profession.NITWIT);

        private static final Multimap<Villager.Profession, Career> careerMap = LinkedListMultimap.create();
        private final Villager.Profession profession;

        static {
            for (Career career : values())
                careerMap.put(career.profession, career);
        }

        Career(Villager.Profession profession) {
            this.profession = profession;
        }

        public Villager.Profession getProfession() {
            return profession;
        }

        public static List<Career> getCareers(Villager.Profession profession) {
            return careerMap.containsKey(profession) ? ImmutableList.copyOf(careerMap.get(profession)) : ImmutableList.of();
        }
    }
}