package minecraft.dailycraft.advancedspyinventory.listener;

import minecraft.dailycraft.advancedspyinventory.utils.Config;
import minecraft.dailycraft.advancedspyinventory.utils.OfflinePlayer;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class PlayerListeners implements Listener
{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Config.reload();

        if (Config.get().get(uuid.toString()) != null)
        {
            player.getInventory().clear();
            player.getEnderChest().clear();

            player.getInventory().setContents(((OfflinePlayer) Config.get().get(uuid.toString())).getInventory());
            player.getEnderChest().setContents(((OfflinePlayer) Config.get().get(uuid.toString())).getEnderChest());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Location location = player.getLocation();

        Config.get().set(uuid.toString(), new OfflinePlayer(
                new OfflinePlayer.Location(location.getWorld().getName(), location.getX(), location.getY(), location.getZ()),
                player.getHealth(), player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), player.getTotalExperience(), player.getFoodLevel(), player.getInventory().getContents(), player.getEnderChest().getContents()));

        Config.save();
    }
}