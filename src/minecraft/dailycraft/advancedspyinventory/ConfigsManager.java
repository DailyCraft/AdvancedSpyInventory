package minecraft.dailycraft.advancedspyinventory;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class ConfigsManager
{
    private final File playersFile;
    private FileConfiguration playersConfig;

    public ConfigsManager(JavaPlugin plugin)
    {

        playersFile = new File(plugin.getDataFolder(), "offline_players.yml");
    }

    public FileConfiguration getOfflinePlayersConfig()
    {
        if (!playersFile.exists())
        {
            try
            {
                if (playersFile.createNewFile())
                {
                    System.out.println("The " + playersFile.getName() + " configuration file has been created");
                }
                else
                {
                    System.out.println("The " + playersFile.getName() + " configuration file could not be created");
                }
            }
            catch (IOException exception)
            {
                exception.printStackTrace();
            }

        }

        playersConfig = YamlConfiguration.loadConfiguration(playersFile);

        return playersConfig;
    }

    public void saveConfig()
    {
        try
        {
            playersConfig.save(playersFile);
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    public void reloadConfig()
    {
        try
        {
            playersConfig.load(playersFile);
        }
        catch (IOException | InvalidConfigurationException exception)
        {
            exception.printStackTrace();
        }
    }
}