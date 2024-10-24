package mc.dailycraft.advancedspyinventory.nms.v1_13_R2;

import mc.dailycraft.advancedspyinventory.inventory.BaseInventory;
import mc.dailycraft.advancedspyinventory.utils.Triplet;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.PacketPlayInUpdateSign;
import net.minecraft.server.v1_13_R2.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_13_R2.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.UUID;

public class NMSHandler implements mc.dailycraft.advancedspyinventory.nms.NMSHandler {
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

    @Override
    public void setBasePotionType(PotionMeta meta, PotionType potionType) {
        meta.setBasePotionData(new PotionData(potionType));
    }
}