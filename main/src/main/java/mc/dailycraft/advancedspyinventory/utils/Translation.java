package mc.dailycraft.advancedspyinventory.utils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mc.dailycraft.advancedspyinventory.Main;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Translation {
    private static final Map<String, Translation> instances = new HashMap<>();
    private static final String DEFAULT_LANGUAGE;
    private static final Table<String, String, String> formatTable = HashBasedTable.create();

    private final String locale;
    private final Locale javaLocale;

    private Translation(String locale) {
        String[] s = (this.locale = locale).split("_");
        javaLocale = s.length == 2 ? new Locale(s[0], s[1]) : new Locale(s[0]);
    }

    public static Translation of(String locale) {
        Translation translation = instances.get(locale);
        return translation == null ? of() : translation;
    }

    public static Translation of(Player player) {
        return of(player.getLocale());
    }

    public static Translation of() {
        Translation translation = instances.get(DEFAULT_LANGUAGE);
        return translation != null ? translation : instances.get("en_us");
    }

    public String format(String key, Object... parameters) {
        String s;

        if ((s = formatTable.get(locale, key)) == null)
            if ((s = formatTable.get(DEFAULT_LANGUAGE, key)) == null)
                if ((s = formatTable.get("en_us", key)) == null)
                    return key;

        try {
            return String.format(javaLocale, s, parameters);
        } catch (IllegalFormatException exception) {
            Main.getInstance().getLogger().severe("The translation of " + key + " as an incorrect format: '" + s + "'! Error: " + exception.getMessage());
            return key;
        }
    }

    public String formatColor(DyeColor color, String key, Object... parameters) {
        return dyeColorToChat(color) + format(key, parameters);
    }

    public String formatColor(DyeColor color) {
        return formatColor(color, "generic.color." + color.name().toLowerCase());
    }

    public String formatYesNo(boolean condition, String key) {
        return format(key, format("generic." + (condition ? "yes" : "no")));
    }

    public String formatCondition(boolean condition, String key, Object... parameters) {
        return condition ? " " + format(key, parameters) : "";
    }

    private static Map<String, String> initializeTranslations(String preKey, Set<Map.Entry<String, JsonElement>> set) {
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : set) {
            if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isString())
                result.put(preKey + (!preKey.isEmpty() && !entry.getKey().isEmpty() ? "." : "") + entry.getKey(), ChatColor.translateAlternateColorCodes('&', entry.getValue().getAsString()));
            else if (entry.getValue().isJsonArray()) {
                for (int i = 0; i < entry.getValue().getAsJsonArray().size(); i++) {
                    JsonElement json = entry.getValue().getAsJsonArray().get(i);

                    if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString())
                        result.put(preKey + (!preKey.isEmpty() && !entry.getKey().isEmpty() ? "." : "") + entry.getKey() + "." + i, ChatColor.translateAlternateColorCodes('&', json.getAsString()));
                }
            } else if (entry.getValue().isJsonObject())
                result.putAll(initializeTranslations(preKey + (!preKey.isEmpty() && !entry.getKey().isEmpty() ? "." : "") + entry.getKey(), entry.getValue().getAsJsonObject().entrySet()));
        }

        return result;
    }

    static {
        DEFAULT_LANGUAGE = Main.getInstance().getConfig().getString("default_language").toLowerCase();

        formatTable.clear();
        instances.clear();

        File langDir = new File(Main.getInstance().getDataFolder(), "lang");

        if (!langDir.exists())
            langDir.mkdirs();

        Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().setPrettyPrinting().create();

        for (String lang : new String[] {"en_us", "fr_fr"}) {
            if (Main.getInstance().getConfig().getBoolean("dynamic_language") || lang.equals(DEFAULT_LANGUAGE)) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(Translation.class.getClassLoader().getResourceAsStream("lang/" + lang + ".json"), StandardCharsets.UTF_8))) {
                    initializeTranslations("", gson.fromJson(reader, JsonObject.class).entrySet()).forEach((key, value) -> formatTable.put(lang, key, value));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }

        Arrays.stream(langDir.listFiles()).filter(file -> file.getName().endsWith(".json")).forEach(file -> {
            String lang = file.getName().replaceFirst("\\..+", "").toLowerCase();

            if (Main.getInstance().getConfig().getBoolean("dynamic_language") || lang.equals(DEFAULT_LANGUAGE)) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                    initializeTranslations("", gson.fromJson(reader, JsonObject.class).entrySet()).forEach((key, value) -> formatTable.put(lang, key, value));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });

        formatTable.rowKeySet().forEach(lang -> instances.put(lang, new Translation(lang)));
    }

    public static String dyeColorToChat(DyeColor color) {
        if (Main.VERSION >= 16) {
            StringBuilder sb = new StringBuilder("§x");

            for (char c : Integer.toHexString(color.getColor().asRGB()).toCharArray())
                sb.append('§').append(c);

            return sb.toString();
        } else {
            switch (color) {
                case WHITE:
                default:
                    return ChatColor.WHITE.toString();
                case ORANGE:
                case BROWN:
                    return ChatColor.GOLD.toString();
                case MAGENTA:
                case PINK:
                    return ChatColor.LIGHT_PURPLE.toString();
                case LIGHT_BLUE:
                    return ChatColor.AQUA.toString();
                case YELLOW:
                    return ChatColor.YELLOW.toString();
                case LIME:
                    return ChatColor.GREEN.toString();
                case GRAY:
                    return ChatColor.DARK_GRAY.toString();
                case LIGHT_GRAY:
                    return ChatColor.GRAY.toString();
                case CYAN:
                    return ChatColor.DARK_AQUA.toString();
                case PURPLE:
                    return ChatColor.DARK_PURPLE.toString();
                case BLUE:
                    return ChatColor.BLUE.toString();
                case GREEN:
                    return ChatColor.DARK_GREEN.toString();
                case RED:
                    return ChatColor.RED.toString();
                case BLACK:
                    return ChatColor.BLACK.toString();
            }
        }
    }
}