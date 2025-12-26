package mc.dailycraft.advancedspyinventory.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import mc.dailycraft.advancedspyinventory.Main;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.registry.RegistryAware;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

public class ItemStackBuilder {
    private static final Map<String, PlayerProfile> headProfiles = new HashMap<>();

    private final ItemStack stack;
    private final ItemMeta meta;

    public ItemStackBuilder(ItemStack stack) {
        this.stack = stack;
        meta = stack.getItemMeta();
    }

    public ItemStackBuilder(ItemStack stack, String displayName) {
        this(stack);
        displayName(displayName);
    }

    public static ItemStackBuilder ofStainedGlassPane(DyeColor color, String displayName) {
            return new ItemStackBuilder(Material.getMaterial(color.name() + "_STAINED_GLASS_PANE"), displayName);
    }

    public ItemStackBuilder(Material material, String displayName) {
        this(new ItemStack(material), displayName);
    }

    public ItemStackBuilder(String headOwner, String displayName) {
        stack = new ItemStack(Material.PLAYER_HEAD);
        meta = stack.getItemMeta();
        displayName(displayName);

        PlayerProfile profile = headProfiles.get(headOwner);

        if (profile == null) {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                UUID uuid;

                if (Bukkit.getOnlineMode()) {
                    uuid = Bukkit.getOfflinePlayer(headOwner).getUniqueId();
                } else {
                    try (Reader reader = new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + headOwner).openStream())) {
                        uuid = UUID.fromString(new Gson().fromJson(reader, JsonObject.class).get("id").getAsString().replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
                    } catch (IOException exception) {
                        throw new RuntimeException(exception);
                    }
                }

                try {
                    headProfiles.put(headOwner, Bukkit.createPlayerProfile(uuid).update().get());
                } catch (InterruptedException | ExecutionException exception) {
                    throw new RuntimeException(exception);
                }
            });
        } else
            ((SkullMeta) meta).setOwnerProfile(profile);
    }

    public ItemStackBuilder(PotionType potionType, String displayName) {
        this(Material.POTION, displayName);
        ((PotionMeta) meta).setBasePotionType(potionType);
        hideAdditionalTooltip();
    }

    public ItemStackBuilder displayName(String displayName) {
        meta.setDisplayName(displayName != null ? ChatColor.RESET.toString() + ChatColor.WHITE + displayName : null);
        if (displayName == null || displayName.isEmpty())
            meta.setHideTooltip(true);
        return this;
    }

    public ItemStackBuilder hideAdditionalTooltip() {
        meta.addItemFlags(ItemFlag.HIDE_BEES);
        meta.addItemFlags(ItemFlag.HIDE_BLOCK_STATE);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        return this;
    }

    public ItemStackBuilder lore(String line, boolean newLine) {
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
        return this;
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

    public <T extends Enum<?>> ItemStackBuilder enumLore(Translation translation, T[] enumeration, T current, @Nullable Function<T, DyeColor> entryColor, String keyStart) {
        Arrays.sort(enumeration, Comparator.comparingInt(Enum::ordinal));

        for (T entry : enumeration) {
            StringBuilder sb = new StringBuilder(current == entry ? "§2\u25ba§a " : "  ");
            if (entryColor != null)
                sb.append(Translation.dyeColorToChat(entryColor.apply(entry)));
            if (current == entry)
                sb.append("§l");

            lore(sb + translation.format(keyStart + "." + entry.name().toLowerCase()));
        }

        return this;
    }

    public <T extends Enum<?>> ItemStackBuilder enumLore(Translation translation, T[] enumeration, T current, String keyStart) {
        return enumLore(translation, enumeration, current, null, keyStart);
    }

    public static <T extends Enum<?>> void enumLoreClick(InventoryClickEvent event, T[] enumeration, T currentValue, Consumer<T> setter) {
        Arrays.sort(enumeration, Comparator.comparingInt(Enum::ordinal));
        int i = currentValue.ordinal();

        if (event.isLeftClick())
            setter.accept(enumeration[++i >= enumeration.length ? 0 : i]);
        else if (event.isRightClick())
            setter.accept(enumeration[--i < 0 ? enumeration.length - 1 : i]);
    }

    public <T extends Keyed & RegistryAware> ItemStackBuilder registryLore(Translation translation, Registry<T> registry, T current, @Nullable Function<T, DyeColor> entryColor, String keyStart) {
        for (T entry : registry) {
            StringBuilder sb = new StringBuilder(current == entry ? "§2\u25ba§a " : "  ");
            if (entryColor != null)
                sb.append(Translation.dyeColorToChat(entryColor.apply(entry)));
            if (current == entry)
                sb.append("§l");

            lore(sb + translation.format(keyStart + "." + entry.getKeyOrThrow().getKey()));
        }

        return this;
    }

    public <T extends Keyed & RegistryAware> ItemStackBuilder registryLore(Translation translation, Registry<T> registry, T current, String keyStart) {
        return registryLore(translation, registry, current, null, keyStart);
    }

    public static <T extends Keyed & RegistryAware> void registryLoreClick(InventoryClickEvent event, Registry<T> registry, T currentValue, Consumer<T> setter) {
        List<T> list = new ArrayList<>();
        registry.iterator().forEachRemaining(list::add);
        int i = list.indexOf(currentValue);

        if (event.isLeftClick())
            setter.accept(list.get(++i >= list.size() ? 0 : i));
        else if (event.isRightClick())
            setter.accept(list.get(--i < 0 ? list.size() - 1 : i));
    }

    public ItemStackBuilder enchant(boolean condition) {
        if (condition) {
            stack.addUnsafeEnchantment(Enchantment.PROTECTION, 1);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        return this;
    }

//    public <T extends ItemMeta> ItemStackBuilder modifyMeta(Consumer<T> consumer) {
//        if (stack.getItemMeta() != null) {
//            T meta = (T) stack.getItemMeta();
//            consumer.accept(meta);
//            stack.setItemMeta(meta);
//        }
//
//        return this;
//    }

    public ItemStack get() {
        stack.setItemMeta(meta);
        return stack;
    }
}