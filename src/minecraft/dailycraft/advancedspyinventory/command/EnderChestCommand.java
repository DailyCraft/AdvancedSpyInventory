package minecraft.dailycraft.advancedspyinventory.command;

import minecraft.dailycraft.advancedspyinventory.inventory.Inventories;
import minecraft.dailycraft.advancedspyinventory.utils.Permissions;
import minecraft.dailycraft.advancedspyinventory.utils.TranslationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EnderChestCommand extends PlayerTabExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            TranslationUtils translation = new TranslationUtils(player);

            if (args.length == 0)
            {
                player.openInventory(Inventories.getEnderChest(player, player.getUniqueId()));
            }
            else if (args.length == 1)
            {
                if (sender.hasPermission(Permissions.ENDER_OTHERS.get()))
                {
                    UUID targetUuid;

                    try
                    {
                        targetUuid = UUID.fromString(args[0]);
                    }
                    catch (IllegalArgumentException exception)
                    {
                        targetUuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
                    }

                    Entity targetEntity = Bukkit.getEntity(targetUuid);

                    if (targetEntity == null || targetEntity instanceof Player)
                    {
                        if (Bukkit.getOfflinePlayer(targetUuid).isOnline() || Bukkit.getOfflinePlayer(targetUuid).hasPlayedBefore())
                            player.openInventory(Inventories.getEnderChest(player, targetUuid));
                        else
                            sender.sendMessage(translation.format("command.error.played_before"));
                    }
                    else
                    {
                        sender.sendMessage(translation.format("command.error.player"));
                    }
                }
                else
                {
                    sender.sendMessage(translation.format("permission.enderchest.other"));
                }

            }
            else
            {
                sender.sendMessage(ChatColor.RED + command.getUsage().replace("<command>", command.getLabel()));
            }
        }
        else
        {
            sender.sendMessage(new TranslationUtils(null).format("command.error.console"));
        }

        return true;
    }
}