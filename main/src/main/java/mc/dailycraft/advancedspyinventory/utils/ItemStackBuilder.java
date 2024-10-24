package mc.dailycraft.advancedspyinventory.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import mc.dailycraft.advancedspyinventory.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ItemStackBuilder {
    private static final Map<String, GameProfile> headProfiles = new HashMap<>();

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
        if (Main.VERSION >= 13)
            return new ItemStackBuilder(Material.getMaterial(color.name() + "_STAINED_GLASS_PANE"), displayName);
        else
            return new ItemStackBuilder(new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, color.getWoolData()), displayName);
    }

    public ItemStackBuilder(Material material, String displayName) {
        this(new ItemStack(material), displayName);
    }

    public ItemStackBuilder(String headOwner, String displayName) {
        stack = Main.VERSION >= 13 ? new ItemStack(Material.PLAYER_HEAD) : new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) 3);
        meta = stack.getItemMeta();
        displayName(displayName);

        try {
            GameProfile profile = headProfiles.get(headOwner);

            if (profile == null) {
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    try {
                        UUID uuid;

                        if (Bukkit.getOnlineMode()) {
                            uuid = Bukkit.getOfflinePlayer(headOwner).getUniqueId();
                        } else {
                            try (Reader reader = new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + headOwner).openStream())) {
                                uuid = UUID.fromString(new Gson().fromJson(reader, JsonObject.class).get("id").getAsString().replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
                            }
                        }

                        try (Reader sessionReader = new InputStreamReader(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "")).openStream())) {
                            JsonObject textureProperty = new Gson().fromJson(sessionReader, JsonObject.class).get("properties").getAsJsonArray().get(0).getAsJsonObject();
                            String value = textureProperty.get("value").getAsString();

                            GameProfile gameProfile = new GameProfile(uuid, headOwner);
                            gameProfile.getProperties().put("textures", new Property("textures", value));
                            headProfiles.put(headOwner, gameProfile);
                        }
                    } catch (Exception exception) {
                        Main.getInstance().getLogger().severe("Error when loading head '" + headOwner + "'! Message: " + exception.getMessage());
                    }
                });
            } else
                Main.NMS.setHeadProfile((SkullMeta) meta, profile);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    public ItemStackBuilder(PotionType potionType, String displayName) {
        this(Material.POTION, displayName);
        Main.NMS.setBasePotionType((PotionMeta) meta, potionType);
        hideAdditionalTooltip();
    }

    public ItemStackBuilder displayName(String displayName) {
        meta.setDisplayName(displayName != null ? ChatColor.RESET.toString() + ChatColor.WHITE + displayName : null);
        if (Main.VERSION >= 20.5 && (displayName == null || displayName.isEmpty()))
            meta.setHideTooltip(true);
        return this;
    }

    public ItemStackBuilder hideAdditionalTooltip() {
        meta.addItemFlags(Main.VERSION >= 20.5 ? ItemFlag.HIDE_ADDITIONAL_TOOLTIP : ItemFlag.valueOf("HIDE_POTION_EFFECTS"));
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

    public <T> ItemStackBuilder enumLore(Translation translation, T[] enumeration, T current, @Nullable Function<T, DyeColor> entryColor, String keyStart) {
        Arrays.sort(enumeration, Comparator.comparingInt(ClassChange::enumOrdinal));

        for (T entry : enumeration) {
            StringBuilder sb = new StringBuilder(current == entry ? "§2\u25ba§a " : "  ");
            if (entryColor != null)
                sb.append(Translation.dyeColorToChat(entryColor.apply(entry)));
            if (current == entry)
                sb.append("§l");

            lore(sb + translation.format(keyStart + "." + ClassChange.enumName(entry).toLowerCase()));
        }

        return this;
    }

    public <T> ItemStackBuilder enumLore(Translation translation, T[] enumeration, T current, String keyStart) {
        return enumLore(translation, enumeration, current, null, keyStart);
    }

    public static <T> void enumLoreClick(InventoryClickEvent event, T[] enumeration, T currentValue, Consumer<T> setter) {
        Arrays.sort(enumeration, Comparator.comparingInt(ClassChange::enumOrdinal));
        int i = ClassChange.enumOrdinal(currentValue);

        if (event.isLeftClick())
            setter.accept(enumeration[++i >= enumeration.length ? 0 : i]);
        else if (event.isRightClick())
            setter.accept(enumeration[--i < 0 ? enumeration.length - 1 : i]);
    }

    public ItemStackBuilder enchant(boolean condition) {
        if (condition) {
            stack.addUnsafeEnchantment(Main.VERSION >= 20.5 ? Enchantment.PROTECTION : Enchantment.getByName("PROTECTION_ENVIRONMENTAL"), 1);
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