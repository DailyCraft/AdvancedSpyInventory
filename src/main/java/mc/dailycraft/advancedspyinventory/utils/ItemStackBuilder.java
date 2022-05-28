package mc.dailycraft.advancedspyinventory.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemStackBuilder implements Cloneable {
    private final ItemStack stack;

    public ItemStackBuilder(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStackBuilder(ItemStack stack, String displayName) {
        this(stack);
        modifyMeta(meta -> meta.setDisplayName(displayName != null ? ChatColor.RESET.toString() + ChatColor.WHITE + displayName : null));
    }

    public ItemStackBuilder(Material material, String displayName) {
        this(new ItemStack(material), displayName);
    }

    public ItemStackBuilder(String headOwner, String displayName) {
        this(Material.PLAYER_HEAD, displayName);
        modifyMeta((Consumer<SkullMeta>) meta -> meta.setOwner(headOwner));
    }

    public ItemStackBuilder(PotionType potionType, String displayName) {
        this(Material.POTION, displayName);
        modifyMeta((Consumer<PotionMeta>) meta -> {
            meta.setBasePotionData(new PotionData(potionType));
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        });
    }

    public ItemStackBuilder(net.minecraft.server.v1_16_R3.ItemStack nmsStack) {
        this(CraftItemStack.asBukkitCopy(nmsStack));
    }

    public ItemStackBuilder lore(String line, boolean newLine) {
        return modifyMeta(meta -> {
            List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();

            if (newLine) {
                StringBuilder lineBuilder = new StringBuilder(ChatColor.GRAY.toString());
                int cCount = 0;
                boolean nextSpace = false;

                for (char c : line.toCharArray()) {
                    cCount++;

                    if (cCount % 40 == 0)
                        nextSpace = true;

                    if (nextSpace && c == ' ') {
                        lore.add(lineBuilder.toString());
                        lineBuilder = new StringBuilder(ChatColor.getLastColors(lore.get(lore.size() - 1)));
                        nextSpace = false;
                    } else
                        lineBuilder.append(c);
                }

                lore.add(lineBuilder.toString());
            } else
                lore.add(ChatColor.GRAY + line);

            meta.setLore(lore);
        });
    }

    public ItemStackBuilder lore(String line) {
        return lore(line, false);
    }

    public ItemStackBuilder lore(boolean condition, String... lines) {
        if (condition)
            for (String s : lines)
                lore(s);

        return this;
    }

    public ItemStackBuilder modifyLore(Player viewer, EntityType entityType) {
        return lore(viewer.hasPermission(Permissions.ENTITY_INFORMATION_MODIFY.get(entityType)), "", Translation.of(viewer).format("interface.information.modify"));
    }

    public ItemStackBuilder switchLore(Player viewer, EntityType entityType) {
        return lore(viewer.hasPermission(Permissions.ENTITY_INFORMATION_MODIFY.get(entityType)), "", Translation.of(viewer).format("interface.information.switch"));
    }

    public ItemStackBuilder enchant(boolean condition) {
        if (condition) {
            stack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            return modifyMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
        }

        return this;
    }

    public <T extends ItemMeta> ItemStackBuilder modifyMeta(Consumer<T> consumer) {
        T meta = (T) stack.getItemMeta();
        consumer.accept(meta);
        stack.setItemMeta(meta);
        return this;
    }

    public ItemStack get() {
        return stack;
    }

    public net.minecraft.server.v1_16_R3.ItemStack nms() {
        return CraftItemStack.asNMSCopy(stack);
    }

    @Override
    protected ItemStackBuilder clone() {
        return new ItemStackBuilder(stack.clone(), stack.getItemMeta().getDisplayName());
    }
}