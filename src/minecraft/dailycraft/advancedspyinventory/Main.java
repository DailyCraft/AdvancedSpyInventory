package minecraft.dailycraft.advancedspyinventory;

import minecraft.dailycraft.advancedspyinventory.command.*;
import minecraft.dailycraft.advancedspyinventory.listener.InventoryListeners;
import minecraft.dailycraft.advancedspyinventory.listener.PlayerListeners;
import minecraft.dailycraft.advancedspyinventory.utils.Config;
import minecraft.dailycraft.advancedspyinventory.utils.OfflinePlayer;
import minecraft.dailycraft.advancedspyinventory.utils.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Main extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        addCommands(new InventoryCommand(), new EnderChestCommand());
        addEvents(new InventoryListeners(), new PlayerListeners());

        for (Permissions perm : Permissions.values())
            getServer().getPluginManager().addPermission(perm.get());

        ConfigurationSerialization.registerClass(OfflinePlayer.class);
        ConfigurationSerialization.registerClass(OfflinePlayer.Location.class);

        saveDefaultConfig();
        saveResource("lang/en_us.lang", true);
        saveResource("lang/fr_fr.lang", true);
    }

    @Override
    public void onDisable()
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            UUID uuid = player.getUniqueId();
            Location location = player.getLocation();

            Config.get().set(uuid.toString(), new OfflinePlayer(
                    new OfflinePlayer.Location(location.getWorld().getName(), location.getX(), location.getY(), location.getZ()),
                    player.getHealth(), player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), player.getTotalExperience(), player.getFoodLevel(), player.getInventory().getContents(), player.getEnderChest().getContents()));
        }

        Config.save();
    }

    public static Main getInstance()
    {
        return getPlugin(Main.class);
    }

    private void addCommands(TabExecutor... tabExecutors)
    {
        for (TabExecutor tabExecutor : tabExecutors)
        {
            PluginCommand command = getCommand(tabExecutor.getClass().getSimpleName().replace("Command", "").toLowerCase());

            command.setExecutor(tabExecutor);
            command.setTabCompleter(tabExecutor);
        }
    }

    private void addEvents(Listener... listeners)
    {
        for (Listener listener : listeners)
        {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }
}