package mc.dailycraft.advancedspyinventory.command;

import mc.dailycraft.advancedspyinventory.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PlayerTabExecutor implements TabExecutor {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return Arrays.stream(Bukkit.getServer().getOfflinePlayers())
                .filter(player -> Main.getInstance().getConfig().getBoolean("show_offline_players") || player.isOnline())
                .map(OfflinePlayer::getName)
                .filter(name -> name.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}