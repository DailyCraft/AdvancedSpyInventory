package minecraft.dailycraft.advancedspyinventory.listerner;

import minecraft.dailycraft.advancedspyinventory.ConfigsManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class PlayerListeners implements Listener
{
    private final ConfigsManager configsManager;
    private final FileConfiguration offlinePlayersConfig;

    public PlayerListeners(JavaPlugin plugin)
    {
        configsManager = new ConfigsManager(plugin);
        offlinePlayersConfig = configsManager.getOfflinePlayersConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        configsManager.reloadConfig();

        if (offlinePlayersConfig.get(uuid.toString()) != null)
        {
            player.getInventory().clear();
            player.getEnderChest().clear();

            try
            {
                player.getInventory().setContents((ItemStack[]) offlinePlayersConfig.get(uuid + ".inventory"));
                player.getEnderChest().setContents((ItemStack[]) offlinePlayersConfig.get(uuid + ".enderchest"));
            }
            catch (ClassCastException exception)
            {
                player.getInventory().setContents(((ArrayList<ItemStack>) offlinePlayersConfig.get(uuid + ".inventory")).toArray(new ItemStack[player.getInventory().getContents().length]));
                player.getEnderChest().setContents(((ArrayList<ItemStack>) offlinePlayersConfig.get(uuid + ".enderchest")).toArray(new ItemStack[player.getEnderChest().getContents().length]));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Location location = player.getLocation();

        offlinePlayersConfig.set(uuid + ".location.world", location.getWorld().getName());
        offlinePlayersConfig.set(uuid + ".location.x", location.getX());
        offlinePlayersConfig.set(uuid + ".location.y", location.getY());
        offlinePlayersConfig.set(uuid + ".location.z", location.getZ());

        offlinePlayersConfig.set(uuid + ".health", event.getPlayer().getHealth());
        offlinePlayersConfig.set(uuid + ".experience", event.getPlayer().getTotalExperience());
        offlinePlayersConfig.set(uuid + ".food", event.getPlayer().getFoodLevel());
        offlinePlayersConfig.set(uuid + ".inventory", player.getInventory().getContents());
        offlinePlayersConfig.set(uuid + ".enderchest", player.getEnderChest().getContents());
        configsManager.saveConfig();
    }
}