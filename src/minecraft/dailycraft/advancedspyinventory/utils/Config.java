package minecraft.dailycraft.advancedspyinventory.utils;

import minecraft.dailycraft.advancedspyinventory.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config
{
    private static final File file = new File(Main.getInstance().getDataFolder(), "offline_players.yml");
    private static final YamlConfiguration config;

    public static YamlConfiguration get()
    {
        return config;
    }

    public static void save()
    {
        try
        {
            config.save(file);
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    public static void reload()
    {
        try
        {
            config.load(file);
        }
        catch (IOException | InvalidConfigurationException exception)
        {
            exception.printStackTrace();
        }
    }

    static
    {
        if (!file.exists())
        {
            try
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            catch (IOException exception)
            {
                exception.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }
}