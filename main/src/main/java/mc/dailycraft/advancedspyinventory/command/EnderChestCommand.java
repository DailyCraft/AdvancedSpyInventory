package mc.dailycraft.advancedspyinventory.command;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.inventory.EnderChestInventory;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import mc.dailycraft.advancedspyinventory.utils.Translation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class EnderChestCommand extends PlayerTabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Translation translation = Translation.of(player);

            if (args.length == 0)
                new EnderChestInventory(player, player.getUniqueId()).open();
            else if (args.length == 1) {
                if (Permissions.ENDER_OTHERS.has(sender)) {
                    UUID targetUuid;

                    try {
                        targetUuid = UUID.fromString(args[0]);
                    } catch (IllegalArgumentException exception) {
                        targetUuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
                    }

                    Entity targetEntity = Main.NMS.getEntity(targetUuid);

                    if (targetEntity == null || targetEntity instanceof Player) {
                        if (Bukkit.getOfflinePlayer(targetUuid).isOnline() || Bukkit.getOfflinePlayer(targetUuid).hasPlayedBefore())
                            new EnderChestInventory(player, targetUuid).open();
                        else
                            sender.sendMessage(translation.format("command.never_connected"));
                    } else
                        sender.sendMessage(translation.format("command.not_player_uuid"));
                } else
                    sender.sendMessage(translation.format("permission.enderchest.other"));
            } else
                sender.sendMessage(ChatColor.RED + command.getUsage().replace("<command>", command.getLabel()));
        } else
            sender.sendMessage(Translation.of().format("command.not_player"));

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1 && Permissions.ENDER_OTHERS.has(sender))
            return super.onTabComplete(sender, command, label, args);

        return Collections.emptyList();
    }
}