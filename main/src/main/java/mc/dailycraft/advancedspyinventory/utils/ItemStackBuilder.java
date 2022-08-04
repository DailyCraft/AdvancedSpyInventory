package mc.dailycraft.advancedspyinventory.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
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
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ItemStackBuilder {
    private static final Map<String, GameProfile> headProfiles = new HashMap<>();
    private static Field headField;
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

        this.<SkullMeta>modifyMeta(meta -> {
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
                                    uuid = UUIDTypeAdapter.fromString(new Gson().fromJson(reader, JsonObject.class).get("id").getAsString());
                                }
                            }

                            try (Reader sessionReader = new InputStreamReader(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(uuid)).openStream())) {
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
                } else {
                    if (headField == null)
                        (headField = meta.getClass().getDeclaredField("profile")).setAccessible(true);

                    headField.set(meta, profile);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public ItemStackBuilder(PotionType potionType, String displayName) {
        this(Material.POTION, displayName);
        this.<PotionMeta>modifyMeta(meta -> {
            meta.setBasePotionData(new PotionData(potionType));
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        });
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

    public <T> ItemStackBuilder enumLore(Translation translation, T[] enumeration, T current, @Nullable Function<T, DyeColor> entryColor, String keyStart) {
        for (T entry : enumeration) {
            StringBuilder sb = new StringBuilder(current == entry ? "§2\u25ba§a " : "  ");
            if (entryColor != null)
                sb.append(Translation.dyeColorToChat(entryColor.apply(entry)));
            if (current == entry)
                sb.append("§l");

            lore(sb + translation.format(keyStart + "." + (((Enum<?>) entry).name().toLowerCase())));
        }

        return this;
    }

    public <T> ItemStackBuilder enumLore(Translation translation, T[] enumeration, T current, String keyStart) {
        return enumLore(translation, enumeration, current, null, keyStart);
    }

    public static <T> void enumLoreClick(InventoryClickEvent event, T[] enumeration, T currentValue, Consumer<T> setter) {
        int i = ((Enum<?>) currentValue).ordinal();

        if (event.isLeftClick())
            setter.accept(enumeration[++i >= enumeration.length ? 0 : i]);
        else if (event.isRightClick())
            setter.accept(enumeration[--i < 0 ? enumeration.length - 1 : i]);
    }

    public ItemStackBuilder enchant(boolean condition) {
        if (condition) {
            stack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            return modifyMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
        }

        return this;
    }

    public <T extends ItemMeta> ItemStackBuilder modifyMeta(Consumer<T> consumer) {
        if (stack.getItemMeta() != null) {
            T meta = (T) stack.getItemMeta();
            consumer.accept(meta);
            stack.setItemMeta(meta);
        }

        return this;
    }

    public ItemStack get() {
        return stack;
    }
}