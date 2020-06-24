package minecraft.dailycraft.advancedspyinventory.listerner;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class PlayerListeners implements Listener
{
    public static Map<UUID, Player> playerMap = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        if (!playerMap.containsKey(event.getPlayer().getUniqueId()))
            return;

        event.getPlayer().getInventory().clear();

        PlayerInventory inv = playerMap.get(event.getPlayer().getUniqueId()).getInventory();
        Inventory ec = playerMap.get(event.getPlayer().getUniqueId()).getEnderChest();

        int i = 0;
        while (i != inv.getSize())
        {
            event.getPlayer().getInventory().setItem(i, inv.getItem(i));
            i++;
        }

        i = 0;
        while (i != ec.getSize())
        {
            event.getPlayer().getEnderChest().setItem(i, ec.getItem(i));
            i++;
        }

        getPlayerMap().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        playerMap.put(event.getPlayer().getUniqueId(), event.getPlayer());
    }

    public static Map<UUID, Player> getPlayerMap()
    {
        return playerMap;
    }
}
