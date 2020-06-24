package minecraft.dailycraft.advancedspyinventory.command;

import minecraft.dailycraft.advancedspyinventory.Main;
import minecraft.dailycraft.advancedspyinventory.TranslationUtils;
import minecraft.dailycraft.advancedspyinventory.gui.InventoryEnderChest;
import minecraft.dailycraft.advancedspyinventory.listerner.PlayerListeners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandEnderChest implements CommandExecutor, TabCompleter
{
    private final Main main;

    public CommandEnderChest(Main main)
    {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {

        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            TranslationUtils translation = new TranslationUtils(player);

            if (args.length == 0)
            {
                player.openInventory(player.getEnderChest());
            }
            else if (args.length == 1)
            {
                if (sender.hasPermission("advancedspyinventory.enderchest.others"))
                {
                    try
                    {
                        player.openInventory(new InventoryEnderChest(player, Bukkit.getPlayer(args[0]).getEnderChest(), Bukkit.getPlayer(args[0]).getName()));
                    }
                    catch (NullPointerException exception)
                    {
                        if (PlayerListeners.getPlayerMap().keySet().size() != 0)
                        {
                            for (UUID uuid : PlayerListeners.getPlayerMap().keySet())
                            {
                                if (Bukkit.getOfflinePlayer(uuid).getName().equals(args[0]))
                                {
                                    player.openInventory(new InventoryEnderChest(player, PlayerListeners.getPlayerMap().get(uuid).getEnderChest(), Bukkit.getOfflinePlayer(uuid).getName()));
                                    return true;
                                }
                            }
                        }

                        player.sendMessage(translation.translate("Player not found.", "Joueur non trouvé."));
                    }
                }
                else
                {
                    player.sendMessage(translation.translate("You do not have permission to open the ender chest of other players", "Vous n'avez pas la permission d'ouvrir le coffre de l'ender des autres joueurs"));
                }
            }
            else
            {
                sender.sendMessage(ChatColor.RED + command.getUsage());
            }
        }
        else
        {
            sender.sendMessage("This command can only be used by players");
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
