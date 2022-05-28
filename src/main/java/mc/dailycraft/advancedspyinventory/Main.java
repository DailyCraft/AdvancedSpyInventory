package mc.dailycraft.advancedspyinventory;

import mc.dailycraft.advancedspyinventory.command.EnderChestCommand;
import mc.dailycraft.advancedspyinventory.command.InventoryCommand;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import mc.dailycraft.advancedspyinventory.utils.Translation;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getCommand("inventory").setExecutor(new InventoryCommand());
        getCommand("enderchest").setExecutor(new EnderChestCommand());

        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        Permissions.init();

        new Metrics(this, 15302);
    }

    public static Main getInstance() {
        return getPlugin(Main.class);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView() instanceof CustomInventoryView) {
            event.setCancelled(true);
            ((CustomInventoryView) event.getView()).getInventory().onClick(event, event.getRawSlot());
        } else if (event.getView().getClass().getName().equals(CustomInventoryView.class.getName())) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage(Translation.of((Player) event.getWhoClicked()).format("interface.reload"));
        }
    }
}