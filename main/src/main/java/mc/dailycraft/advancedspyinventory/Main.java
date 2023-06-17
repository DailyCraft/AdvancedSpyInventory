package mc.dailycraft.advancedspyinventory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mc.dailycraft.advancedspyinventory.command.EnderChestCommand;
import mc.dailycraft.advancedspyinventory.command.InventoryCommand;
import mc.dailycraft.advancedspyinventory.inventory.entity.EntityInventory;
import mc.dailycraft.advancedspyinventory.nms.NMSHandler;
import mc.dailycraft.advancedspyinventory.utils.CustomInventoryView;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import mc.dailycraft.advancedspyinventory.utils.Translation;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Main extends JavaPlugin implements Listener {
    public static NMSHandler NMS;
    public static int VERSION = Integer.parseInt(Bukkit.getBukkitVersion().split("\\.")[1].replaceFirst("-.+", ""));

    private boolean updateAvailable = false;

    @Override
    public void onEnable() {
        checkUpdateAvailable();

        try {
            NMS = (NMSHandler) Class.forName(getServer().getClass().getPackage().getName().replaceFirst(".+\\.", "mc.dailycraft.advancedspyinventory.nms.") + ".NMSHandler").getConstructor().newInstance();

            if (updateAvailable)
                getLogger().info("An update is available, go to the website to download the new plugin version: " + getDescription().getWebsite());

            getCommand("inventory").setExecutor(new InventoryCommand());
            getCommand("enderchest").setExecutor(new EnderChestCommand());

            getServer().getPluginManager().registerEvents(this, this);
            saveDefaultConfig();
            Permissions.init();
        } catch (ClassNotFoundException exception) {
            getLogger().severe("The current version of the plugin doesn't support your server version.");

            if (updateAvailable)
                getLogger().severe("Please update the plugin: " + getDescription().getWebsite());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage("§c[AdvancedSpyInventory] The current version of the plugin doesn't support your server version.");
        if (updateAvailable)
            sender.sendMessage("§c[AdvancedSpyInventory] Please update the plugin: §e" + getDescription().getWebsite());
        return true;
    }

    private void checkUpdateAvailable() {
        try {
            URLConnection connection = new URL("https://api.modrinth.com/v2/project/advancedspyinventory/version?game_versions=[%22" + Bukkit.getBukkitVersion().split("-")[0] + "%22]").openConnection();
            connection.setRequestProperty("User-Agent", "AdvancedSpyInventory/" + getDescription().getVersion());

            try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                JsonArray json = new Gson().fromJson(reader, JsonArray.class);

                if (json.size() == 0 || json.get(0).getAsJsonObject().get("version_number").getAsString().equals(getDescription().getVersion()))
                    return;

                String fileName = json.get(0).getAsJsonObject().getAsJsonArray("files").get(0).getAsJsonObject().get("filename").getAsString();

                connection = new URL("https://api.curseforge.com/v1/mods/388970/files").openConnection();
                connection.setRequestProperty("User-Agent", "AdvancedSpyInventory/" + getDescription().getVersion());
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("x-api-key", "$2a$10$jxJEM82j19MyeGsbyiQOHe.flh4Tn2gPK8ZitAS7VQxZK2Y2FFJo6");

                try (InputStreamReader cfReader = new InputStreamReader(connection.getInputStream())) {
                    JsonObject cfJson = new Gson().fromJson(cfReader, JsonObject.class);

                    for (JsonElement element : cfJson.getAsJsonArray("data")) {
                        if (element.getAsJsonObject().get("fileName").getAsString().equals(fileName)) {
                            updateAvailable = true;
                            return;
                        }
                    }
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}