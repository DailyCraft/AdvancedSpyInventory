package minecraft.dailycraft.advancedspyinventory.command;

import minecraft.dailycraft.advancedspyinventory.Main;
import minecraft.dailycraft.advancedspyinventory.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class PlayerTabExecutor implements TabExecutor
{
    @Override
    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        List<String> list = new ArrayList<>();

        sender.getServer().getOnlinePlayers().forEach(player ->
        {
            if (player.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(player.getName());
        });

        if (Main.getInstance().getConfig().getBoolean("show_offline_players"))
            for (String uuid : Config.get().getKeys(false))
                if (!Bukkit.getOfflinePlayer(UUID.fromString(uuid)).isOnline() && Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    list.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());

        return list;
    }
}