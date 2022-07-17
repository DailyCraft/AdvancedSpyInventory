package mc.dailycraft.advancedspyinventory;

import mc.dailycraft.advancedspyinventory.command.EnderChestCommand;
import mc.dailycraft.advancedspyinventory.command.InventoryCommand;
import mc.dailycraft.advancedspyinventory.inventory.entity.EntityInventory;
import mc.dailycraft.advancedspyinventory.nms.NMSHandler;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import mc.dailycraft.advancedspyinventory.utils.Translation;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    public static NMSHandler NMS;
    public static int VERSION = Integer.parseInt(Bukkit.getBukkitVersion().split("\\.")[1].replaceFirst("-.+", ""));

    @Override
    public void onEnable() {
        try {
            NMS = (NMSHandler) Class.forName(getServer().getClass().getPackage().getName().replaceFirst(".+\\.", "mc.dailycraft.advancedspyinventory.nms.") + ".NMSHandler").getConstructor().newInstance();
        } catch (ClassNotFoundException exception) {
            getLogger().severe("The current server version isn't supported by the plugin.");
            getLogger().severe("You can go to the website " + getDescription().getWebsite() + " to check if a version of the plugin support your server version.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } catch (Exception exception) {
            exception.printStackTrace();
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

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getOpenInventory() instanceof CustomInventoryView) {
                    CustomInventoryView view = (CustomInventoryView) player.getOpenInventory();
                    if (view.getContainer() instanceof EntityInventory && ((EntityInventory<?>) view.getContainer()).entity == event.getEntity()) {
                        player.closeInventory();
                        player.sendMessage(Translation.of(player).format("interface.dead"));
                    }
                }
            }
        }
    }
}