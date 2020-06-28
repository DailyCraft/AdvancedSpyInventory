package minecraft.dailycraft.advancedspyinventory.command;

import minecraft.dailycraft.advancedspyinventory.ConfigsManager;
import minecraft.dailycraft.advancedspyinventory.TranslationUtils;
import minecraft.dailycraft.advancedspyinventory.gui.InventoryEnderChest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class CommandEnderChest implements CommandExecutor
{
    private final JavaPlugin plugin;

    public CommandEnderChest(JavaPlugin plugin)
    {
        this.plugin = plugin;
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
                    if (Bukkit.getOfflinePlayer(args[0]).isOnline())
                    {
                        player.openInventory(new InventoryEnderChest(player, Bukkit.getPlayer(args[0]).getUniqueId(), plugin));
                    }
                    else
                    {
                        FileConfiguration playersConfig = new ConfigsManager(plugin).getOfflinePlayersConfig();

                        for (String uuid : playersConfig.getKeys(false))
                        {
                            if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName().equals(args[0]))
                            {
                                player.openInventory(new InventoryEnderChest(player, UUID.fromString(uuid), plugin));
                                return true;
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
}