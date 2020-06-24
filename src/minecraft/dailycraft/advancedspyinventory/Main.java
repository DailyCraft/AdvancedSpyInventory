package minecraft.dailycraft.advancedspyinventory;

import minecraft.dailycraft.advancedspyinventory.command.CommandEnderChest;
import minecraft.dailycraft.advancedspyinventory.command.CommandInventory;
import minecraft.dailycraft.advancedspyinventory.listerner.InventoryListeners;
import minecraft.dailycraft.advancedspyinventory.listerner.PlayerListeners;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        saveDefaultConfig();

        addCommand("inventory", new CommandInventory(this), new CommandInventory(this));
        addCommand("enderchest", new CommandEnderChest(this), new CommandEnderChest(this));

        addEvents(new InventoryListeners(), new PlayerListeners());
    }

    private void addCommand(String name, CommandExecutor executor, TabCompleter completer)
    {
        PluginCommand command = getCommand(name);

        command.setExecutor(executor);
        command.setTabCompleter(completer);
    }

    private void addEvents(Listener... listeners)
    {
        for (Listener listener : listeners)
        {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }
}