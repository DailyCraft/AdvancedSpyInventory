package minecraft.dailycraft.advancedspyinventory.utils;

import minecraft.dailycraft.advancedspyinventory.Main;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TranslationUtils
{
    private final Player sender;

    public TranslationUtils(Player sender)
    {
        this.sender = sender;
    }

    public String format(String key, Object... parameters)
    {
        File lang = new File(Main.getInstance().getDataFolder(), "lang");

        if (!lang.exists())
            lang.mkdir();

        if (sender != null)
        {
            for (int i = 0; i < lang.listFiles().length; ++i)
            {
                File file = lang.listFiles()[i];

                if (file.getName().startsWith(sender.getLocale()))
                {
                    String result = getString(file, key, parameters);

                    if (!result.equals(key))
                        return result;
                }
            }
        }
        else
        {
            for (int i = 0; i < lang.listFiles().length; ++i)
            {
                File file = lang.listFiles()[i];

                if (file.getName().startsWith(Main.getInstance().getConfig().getString("console_language")))
                {
                    String result = getString(file, key, parameters);

                    if (!result.equals(key))
                        return result;
                }
            }
        }

        for (int i = 0; i < lang.listFiles().length; ++i)
        {
            File file = lang.listFiles()[i];

            if (file.getName().startsWith("en_us"))
            {
                return getString(file, key, parameters);
            }
        }

        return key;
    }

    public boolean textMatches(String key, String formatted)
    {
        return formatted.matches(format(key, ".+", ".+", ".+", ".+", ".+"));
    }

    public Player getSender()
    {
        return sender;
    }

    private String getString(File file, String key, Object[] parameters)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

            String line = reader.readLine();

            while (line != null)
            {
                if (line.startsWith(key))
                {
                    return String.format(line.replace(key + "=", ""), parameters);
                }

                line = reader.readLine();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return key;
    }
}