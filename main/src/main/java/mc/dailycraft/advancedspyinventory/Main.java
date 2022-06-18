package mc.dailycraft.advancedspyinventory;

import mc.dailycraft.advancedspyinventory.command.EnderChestCommand;
import mc.dailycraft.advancedspyinventory.command.InventoryCommand;
import mc.dailycraft.advancedspyinventory.nms.NMSHandler;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import mc.dailycraft.advancedspyinventory.utils.Translation;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

public class Main extends JavaPlugin implements Listener {
    public static NMSHandler NMS;
    public static int VERSION = Integer.parseInt(Bukkit.getBukkitVersion().split("\\.")[1].replaceFirst("-.+", ""));

    @Override
    public void onEnable() {
        try {
            NMS = (NMSHandler) Class.forName("mc.dailycraft.advancedspyinventory.nms." + Bukkit.getServer().getClass().getPackage().getName().replaceFirst(".+\\.", "") + ".NMSHandler").getConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException exception) {
            getLogger().severe("The current server version isn't supported by the plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

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
            ((CustomInventoryView) event.getView()).getContainer().onClick(event, event.getRawSlot());
        } else if (event.getView().getClass().getName().equals(CustomInventoryView.class.getName())) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage(Translation.of((Player) event.getWhoClicked()).format("interface.reload"));
        }
    }
}