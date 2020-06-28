package minecraft.dailycraft.advancedspyinventory.command;

import minecraft.dailycraft.advancedspyinventory.ConfigsManager;
import minecraft.dailycraft.advancedspyinventory.TranslationUtils;
import minecraft.dailycraft.advancedspyinventory.gui.InventoryPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class CommandInventory implements CommandExecutor
{
    private final JavaPlugin plugin;

    public CommandInventory(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            if (args.length == 1)
            {
                Player player = (Player) sender;

                if (Bukkit.getOfflinePlayer(args[0]).isOnline())
                {
                    player.openInventory(new InventoryPlayer(player, Bukkit.getPlayer(args[0]).getUniqueId(), plugin));
                }
                else
                {
                    FileConfiguration playersConfig = new ConfigsManager(plugin).getOfflinePlayersConfig();

                    for (String uuid : playersConfig.getKeys(false))
                    {
                        if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName().equals(args[0]))
                        {
                            player.openInventory(new InventoryPlayer(player, UUID.fromString(uuid), plugin));
                            return true;
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
}