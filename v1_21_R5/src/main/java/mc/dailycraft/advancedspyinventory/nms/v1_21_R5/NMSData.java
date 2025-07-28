package mc.dailycraft.advancedspyinventory.nms.v1_21_R5;

import com.mojang.serialization.Codec;
import mc.dailycraft.advancedspyinventory.Main;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.*;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R5.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.IntUnaryOperator;

public class NMSData extends mc.dailycraft.advancedspyinventory.nms.NMSData {
    private Method paperLoadMethod;
    private Field paperInputField;

    public NMSData(UUID playerUuid) {
        super(playerUuid);
    }

    @Override
    public int getInt(String id) {
        return getData().getIntOr(id, 0);
    }

    @Override
    public void putInt(String id, int value) {
        CompoundTag data = getDataInput();
        data.putInt(id, value);
        saveData(data);
    }

    @Override
    public long getLong(String id) {
        return getData().getLongOr(id, 0);
    }

    @Override
    public void putLong(String id, long value) {
        CompoundTag data = getDataInput();
        data.putLong(id, value);
        saveData(data);
    }

    @Override
    public float getFloat(String id) {
        return getData().getFloatOr(id, 0);
    }

    @Override
    public void putFloat(String id, float value) {
        CompoundTag data = getDataInput();
        data.putFloat(id, value);
        saveData(data);
    }

    @Override
    public String getString(String id) {
        return getData().getStringOr(id, "");
    }

    @Override
    public void putString(String id, String value) {
        CompoundTag data = getDataInput();
        data.putString(id, value);
        saveData(data);
    }

    @Override
    public double[] getList(String id) {
        return getData().listOrEmpty(id, Codec.DOUBLE).stream().mapToDouble(d -> d).toArray();
    }

    @Override
    public void putList(String id, double[] value, boolean isFloat) {
        CompoundTag data = getDataInput();
        ListTag list = new ListTag();
        for (double v : value)
            list.add(isFloat ? FloatTag.valueOf((float) v) : DoubleTag.valueOf(v));
        data.put(id, list);
        saveData(data);
    }

    @Override
    public ItemStack[] getArray(String id, int size, IntUnaryOperator slotConversion) {
        ItemStack[] array = new ItemStack[size];
        Arrays.fill(array, new ItemStack(Material.AIR));

        for (ItemStackWithSlot item : getData().listOrEmpty(id, ItemStackWithSlot.CODEC))
            array[slotConversion.applyAsInt(item.slot())] = CraftItemStack.asBukkitCopy(item.stack());

        return array;
    }

    @Override
    public void setInArray(String id, int slot, ItemStack stack) {
        CompoundTag data = getDataInput();

        ListTag list = data.getListOrEmpty(id);

        for (int i = 0; i < list.size(); i++) {
            if (list.getCompoundOrEmpty(i).getByteOr("Slot", (byte) 0) == slot) {
                list.remove(i);
                break;
            }
        }

        if (stack.getType() != Material.AIR) {
            list.add(saveWithCodec(ItemStackWithSlot.CODEC,
                    new ItemStackWithSlot(slot, CraftItemStack.asNMSCopy(stack))));
        }

        saveData(data);
    }

    private RegistryAccess registryAccess() {
        return ((CraftWorld) Bukkit.getWorlds().getFirst()).getHandle().registryAccess();
    }

    @Override
    public float getMaxHealth() {
        for (AttributeInstance.Packed pack : getData().listOrEmpty("attributes", AttributeInstance.Packed.CODEC))
            if (pack.attribute() == Attributes.MAX_HEALTH)
                return (float) pack.baseValue();

        return -1;
    }

    @Override
    public void setMaxHealth(float maxHealth) {
        CompoundTag data = getDataInput();
        ListTag list = data.getListOrEmpty("attributes");

        for (Tag nbt : list) {
            if (((CompoundTag) nbt).getStringOr("id", "").equals("minecraft:max_health")) {
                list.remove(nbt);
                break;
            }
        }

        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", "minecraft:max_health");
        nbt.putFloat("base", maxHealth);
        list.add(nbt);
        saveData(data);
    }

    @Override
    public ItemStack getEquipment(EquipmentSlot slot) {
        return getData().read("equipment", EntityEquipment.CODEC)
                .map(equipment -> CraftItemStack.asBukkitCopy(equipment.get(CraftEquipmentSlot.getNMS(slot))))
                .orElseGet(() -> new ItemStack(Material.AIR));
    }

    @Override
    public void setEquipment(EquipmentSlot slot, ItemStack stack) {
        String name = switch (slot) {
            case HEAD -> "head";
            case CHEST -> "chest";
            case LEGS -> "legs";
            case FEET -> "feet";
            case OFF_HAND -> "offhand";
            default -> null;
        };

        CompoundTag data = getDataInput();
        if (!data.contains("equipment"))
            data.put("equipment", new CompoundTag());
        if (stack == null || stack.getType() == Material.AIR)
            data.getCompoundOrEmpty("equipment").remove(name);
        else {
            data.getCompoundOrEmpty("equipment").put(name,
                    saveWithCodec(net.minecraft.world.item.ItemStack.CODEC, CraftItemStack.asNMSCopy(stack)));
        }
        saveData(data);
    }

    private TagValueInput getData() {
        PlayerDataStorage io = ((CraftServer) Bukkit.getServer()).getHandle().playerIo;

        try {
            return (TagValueInput) io
                    .load(playerUuid.toString(), playerUuid.toString(), ProblemReporter.DISCARDING, registryAccess())
                    .orElseThrow();
        } catch (NoSuchMethodError error) { // Paper
            try {
                if (paperLoadMethod == null)
                    paperLoadMethod = PlayerDataStorage.class.getMethod("load", String.class, String.class, ProblemReporter.class);
                CompoundTag tag = ((Optional<CompoundTag>) paperLoadMethod.invoke(io, playerUuid.toString(), playerUuid.toString(), ProblemReporter.DISCARDING)).orElseThrow();
                return (TagValueInput) TagValueInput.create(ProblemReporter.DISCARDING, registryAccess(), tag);
            } catch (ReflectiveOperationException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    private CompoundTag getDataInput() {
        try {
            return getData().input;
        } catch (IllegalAccessError error) { // Paper
            try {
                if (paperInputField == null)
                    (paperInputField = TagValueInput.class.getDeclaredField("input")).setAccessible(true);
                return (CompoundTag) paperInputField.get(getData());
            } catch (ReflectiveOperationException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    private <T> CompoundTag saveWithCodec(Codec<T> codec, T element) {
        TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        output.store("output", codec, element);
        return output.buildResult().getCompoundOrEmpty("output");
    }

    private void saveData(CompoundTag data) {
        if (getOfflinePlayer().isOnline()) {
            TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
            output.buildResult().merge(data);
            ((CraftPlayer) getOfflinePlayer().getPlayer()).getHandle().saveWithoutId(output);
        } else {
            Path playerDir = ((CraftServer) Bukkit.getServer()).getHandle().playerIo.getPlayerDir().toPath();

            try {
                Path file = Files.createTempFile(playerDir, playerUuid + "-", ".dat");
                NbtIo.writeCompressed(data, file);
                Util.safeReplaceFile(playerDir.resolve(playerUuid + ".dat"), file, playerDir.resolve(playerUuid + ".dat_old"));
            } catch (IOException exception) {
                Main.getInstance().getLogger().severe("Failed to save player data for " + playerUuid);
                throw new RuntimeException(exception);
            }
        }
    }
}