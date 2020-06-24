package minecraft.dailycraft.advancedspyinventory.command;

import minecraft.dailycraft.advancedspyinventory.Main;
import minecraft.dailycraft.advancedspyinventory.TranslationUtils;
import minecraft.dailycraft.advancedspyinventory.listerner.PlayerListeners;
import minecraft.dailycraft.advancedspyinventory.gui.InventoryPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandInventory implements CommandExecutor, TabCompleter
{
    private final Main main;

    public CommandInventory(Main main)
    {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            if (args.length == 1)
            {
                Player player = (Player) sender;

                try
                {
                    player.openInventory(new InventoryPlayer(player, Bukkit.getPlayer(args[0]).getInventory()));
                }
                catch (NullPointerException exception)
                {
                    if (PlayerListeners.getPlayerMap().keySet().size() != 0)
                    {
                        for (UUID uuid : PlayerListeners.getPlayerMap().keySet())
                        {
                            if (Bukkit.getOfflinePlayer(uuid).getName().equals(args[0]))
                            {
                                player.openInventory(new InventoryPlayer(player, PlayerListeners.getPlayerMap().get(uuid).getInventory()));
                                return true;
                            }
                        }
                    }

                    player.sendMessage(new TranslationUtils(player).translate("Player not found.", "Joueur non trouvé."));
                }
            }
            else
            {
                sender.sendMessage(ChatColor.RED + command.getUsage());
            }
        }
        else
        {
            sender.sendMessage("This command can only be used by players.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        List<String> name = new ArrayList<>();

        for (Player player : sender.getServer().getOnlinePlayers())
        {
            name.add(player.getName());
        }

        if (main.getConfig().getBoolean("show_offline_players"))
        {
            for (UUID uuid : PlayerListeners.getPlayerMap().keySet())
            {
                name.add(Bukkit.getOfflinePlayer(uuid).getName());
            }
        }

        return name;
    }
}
