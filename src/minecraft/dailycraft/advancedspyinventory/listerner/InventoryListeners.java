package minecraft.dailycraft.advancedspyinventory.listerner;

import minecraft.dailycraft.advancedspyinventory.TranslationUtils;
import minecraft.dailycraft.advancedspyinventory.gui.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListeners implements Listener
{
    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        Inventory inv = event.getInventory();
        int slot = event.getSlot();
        HumanEntity player = event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        TranslationUtils translation = new TranslationUtils((Player) player);

        if (inv.getName().endsWith("§r§e's Inventory") || inv.getName().startsWith("§eInventaire de §4§l"))
        {
            if (event.getInventory().getName().contains(player.getName()) || ((slot >= 40 && slot <= 43) || slot >= 45))
            {
                event.setCancelled(true);
            }

            if (!player.hasPermission("advancedspyinventory.inventory.modify"))
            {
                event.setCancelled(true);
                player.sendMessage(translation.translate("You do not have permission to modify the inventory", "Vous n'avez pas la permssion de modifier l'inventaire"));
            }

            if (slot == 47)
            {
                if (!item.equals(InventoryUtils.getVoidItem()))
                {
                    if (player.hasPermission("advancedspyinventory.inventory.location.teleport"))
                    {
                        player.teleport(new Location(
                                Bukkit.getWorld(item.getItemMeta().getLore().get(0).substring(8)),
                                Double.parseDouble(item.getItemMeta().getLore().get(1).substring(4)),
                                Double.parseDouble(item.getItemMeta().getLore().get(2).substring(4)),
                                Double.parseDouble(item.getItemMeta().getLore().get(3).substring(4))));

                        player.closeInventory();
                    }
                    else
                    {
                        player.sendMessage(translation.translate("You do not have permission to teleport to the player", "Vous n'avez pas la permission de vous téléporter à ce joueur"));
                    }
                }
            }
            else if (slot == 49)
            {
                if (player.hasPermission("advancedspyinventory.inventory.clear"))
                {
                    inv.clear();
                }
                else
                {
                    player.sendMessage(translation.translate("You do not have permission to clear the inventory", "Vous n'avez pas la permission de vider l'inventaire"));
                }
            }
        }
        else if (inv.getName().endsWith("§r§e's Ender Chest") || inv.getName().startsWith("§eCoffre de l'Ender de §4§l"))
        {
            if (slot >= inv.getSize() - 9)
            {
                event.setCancelled(true);
            }

            if (!player.hasPermission("advancedspyinventory.enderchest.modify") && inv.getName().contains(player.getName()))
            {
                if (slot <= inv.getSize() - 10)
                {
                    event.setCancelled(true);
                    player.sendMessage(translation.translate("You do not have permission to modify your ender chest", "Vous n'avez pas la permssion de modifier votre coffre de l'ender"));
                }
            }

            if (!player.hasPermission("advancedspyinventory.enderchest.others.modify") && !inv.getName().contains(player.getName()))
            {
                if (slot <= inv.getSize() - 10)
                {
                    event.setCancelled(true);
                    player.sendMessage(translation.translate("You do not have permission to modify the ender chest", "Vous n'avez pas la permission de modifier le coffre de l'ender"));
                }
            }

            if (slot == inv.getSize() - 5)
            {
                if (player.hasPermission("advancedspyinventory.enderchest.clear"))
                {
                    inv.clear();
                }
                else
                {
                    player.sendMessage(translation.translate("You do not have permission to clear the ender chest", "Vous n'avez pas la permission de vider le coffre de l'ender"));
                }
            }
        }
    }
}