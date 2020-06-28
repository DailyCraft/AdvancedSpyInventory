package minecraft.dailycraft.advancedspyinventory.command;

import minecraft.dailycraft.advancedspyinventory.ConfigsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefaultTabCompleter implements TabCompleter
{
    private final JavaPlugin plugin;

    public DefaultTabCompleter(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        List<String> list = new ArrayList<>();

        for (Player player : sender.getServer().getOnlinePlayers())
        {
            list.add(player.getName());
        }

        if (plugin.getConfig().getBoolean("show_offline_players"))
        {
            for (String uuid : new ConfigsManager(plugin).getOfflinePlayersConfig().getKeys(false))
            {
                if (!Bukkit.getOfflinePlayer(UUID.fromString(uuid)).isOnline())
                    list.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
            }
        }

        return list;
    }
}