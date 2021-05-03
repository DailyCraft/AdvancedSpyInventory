package minecraft.dailycraft.advancedspyinventory.listener;

import minecraft.dailycraft.advancedspyinventory.utils.Permissions;
import minecraft.dailycraft.advancedspyinventory.utils.TranslationUtils;
import minecraft.dailycraft.advancedspyinventory.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
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
    public void onInventoryClick(InventoryClickEvent event)
    {
        Inventory inv = event.getInventory();
        int rawSlot = event.getRawSlot();
        HumanEntity human = event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        int invSize = inv.getSize();

        TranslationUtils translation = new TranslationUtils((Player) human);
        InventoryUtils.InformationItems informationItems = new InventoryUtils.InformationItems(translation);

        if (translation.textMatches("interface.inventory.name", inv.getName()))
        {
            if (!human.hasPermission(Permissions.INVENTORY_MODIFY.get()))
            {
                event.setCancelled(true);
                human.sendMessage(translation.format("permission.inventory.modify"));
            }

            if (event.getInventory().getName().contains(human.getName()) || ((rawSlot >= 40 && rawSlot <= 43) || (rawSlot >= invSize - 9 && rawSlot < invSize)))
            {
                event.setCancelled(true);

                if (rawSlot == invSize - 7)
                {
                    if (!currentItem.equals(CraftItemStack.asBukkitCopy(InventoryUtils.getVoidItem())))
                    {
                        if (human.hasPermission(Permissions.INVENTORY_LOCATION_TELEPORT.get()))
                        {
                            human.teleport(new Location(
                                    Bukkit.getWorld(currentItem.getItemMeta().getLore().get(0).substring(8, currentItem.getItemMeta().getLore().get(0).lastIndexOf(" ("))),
                                    Double.parseDouble(currentItem.getItemMeta().getLore().get(1).substring(4).replace(',', '.')),
                                    Double.parseDouble(currentItem.getItemMeta().getLore().get(2).substring(4).replace(',', '.')),
                                    Double.parseDouble(currentItem.getItemMeta().getLore().get(3).substring(4).replace(',', '.'))));

                            human.closeInventory();
                        }
                        else
                        {
                            human.sendMessage(translation.format("permission.inventory.location"));
                        }
                    }
                }
                else if (rawSlot == invSize - 5)
                {
                    if (human.hasPermission(Permissions.INVENTORY_CLEAR.get()))
                    {
                        inv.clear();
                    }
                    else
                    {
                        human.sendMessage(translation.format("permission.inventory.clear"));
                    }
                }
                else if (rawSlot == invSize - 1)
                {
                    human.closeInventory();
                    ((Player) human).chat("/advancedspyinventory:enderchest " + event.getCurrentItem().getItemMeta().getLore().get(0));
                }
            }
            else
            {
                if (rawSlot == 36)
                    a(event, informationItems.helmet);
                else if (rawSlot == 37)
                    a(event, informationItems.chestplate);
                else if (rawSlot == 38)
                    a(event, informationItems.leggings);
                else if (rawSlot == 39)
                    a(event, informationItems.boots);
                else if (rawSlot == 44)
                    a(event, informationItems.offHand);
            }
        }
        else if (translation.textMatches("interface.enderchest.name", inv.getName()))
        {
            if (rawSlot >= invSize - 9 && rawSlot < invSize)
            {
                event.setCancelled(true);
            }

            if ((!human.hasPermission(Permissions.ENDER_MODIFY.get()) && inv.getName().contains(human.getName())) || (!human.hasPermission(Permissions.ENDER_OTHERS_MODIFY.get()) && !inv.getName().contains(human.getName())))
            {
                if (rawSlot <= invSize - 10)
                {
                    event.setCancelled(true);
                    human.sendMessage(translation.format("permission.enderchest.modify"));
                }
            }

            if (rawSlot == invSize - 9)
            {
                human.closeInventory();
                ((Player) human).chat("/advancedspyinventory:inventory " + event.getCurrentItem().getItemMeta().getLore().get(0));
            }
            else if (rawSlot == invSize - 5)
            {
                if (human.hasPermission(Permissions.ENDER_CLEAR.get()))
                {
                    inv.clear();
                }
                else
                {
                    human.sendMessage(translation.format("permission.enderchest.clear"));
                }
            }
        }
        else if (translation.textMatches("interface.entity.name", inv.getName()))
        {
            if (!human.hasPermission(Permissions.INVENTORY_MODIFY.get()))
                event.setCancelled(true);

            if (rawSlot == invSize - 17)
                a(event, InventoryUtils.InformationItems.addWarning(informationItems.helmet, translation));
            else if (rawSlot == invSize - 16)
                a(event, InventoryUtils.InformationItems.addWarning(informationItems.chestplate, translation));
            else if (rawSlot == invSize - 15)
                a(event, InventoryUtils.InformationItems.addWarning(informationItems.leggings, translation));
            else if (rawSlot == invSize - 14)
                a(event, InventoryUtils.InformationItems.addWarning(informationItems.boots, translation));
            else if (rawSlot == invSize - 12)
                a(event, InventoryUtils.InformationItems.addWarning(informationItems.mainHand, translation));
            else if (rawSlot == invSize - 11)
                a(event, InventoryUtils.InformationItems.addWarning(informationItems.offHand, translation));
            else if ((rawSlot >= invSize - 10 && rawSlot < invSize) || rawSlot == invSize - 13 || rawSlot == invSize - 18)
            {
                event.setCancelled(true);

                if (rawSlot == invSize - 7)
                {
                    human.teleport(new Location(
                            Bukkit.getWorld(currentItem.getItemMeta().getLore().get(0).substring(8, currentItem.getItemMeta().getLore().get(0).lastIndexOf(" ("))),
                            Double.parseDouble(currentItem.getItemMeta().getLore().get(1).substring(4).replace(',', '.')),
                            Double.parseDouble(currentItem.getItemMeta().getLore().get(2).substring(4).replace(',', '.')),
                            Double.parseDouble(currentItem.getItemMeta().getLore().get(3).substring(4).replace(',', '.'))));

                    human.closeInventory();
                }
                else if (rawSlot == invSize - 5)
                {
                    inv.clear();
                }
            }

            if (invSize == 9 * 3)
            {
                if (inv.getItem(3).equals(CraftItemStack.asBukkitCopy(InventoryUtils.getVoidItem()))) // Enderman
                {
                    if (rawSlot <= 8 && rawSlot != 4)
                        event.setCancelled(true);
                }
                else // Horse
                {
                    if (rawSlot <= 8)
                    {
                        event.setCancelled(true);

                        if (rawSlot == 3)
                            a(event, informationItems.saddle);
                        else if (rawSlot == 5)
                            a(event, informationItems.horseArmor);
                    }
                }
            }
            else if (invSize == 9 * 4) // Villager
            {
                if ((rawSlot >= 0 && rawSlot <= 1) || (rawSlot >= 7 && rawSlot <= 11) || (rawSlot >= 15 && rawSlot <= 17))
                    event.setCancelled(true);
            }
            else if (invSize == 9 * 6) // Donkey, mule or llama
            {
                if (inv.getItem(12) != null && inv.getItem(12).equals(CraftItemStack.asBukkitCopy(InventoryUtils.getVoidItem())) && rawSlot == 13)
                    event.setCancelled(true);

                if (currentItem.equals(CraftItemStack.asBukkitCopy(InventoryUtils.getVoidItem())))
                    event.setCancelled(true);

                if (inv.getItem(30).equals(CraftItemStack.asBukkitCopy(informationItems.saddle))) // Donkey and mule interactions
                {
                    if (rawSlot == 30)
                        a(event, informationItems.saddle);
                    else if (rawSlot == 32)
                        a(event, InventoryUtils.InformationItems.addWarning(informationItems.horseArmor, translation));
                }
                else if (inv.getItem(30).equals(CraftItemStack.asBukkitCopy(InventoryUtils.InformationItems.addWarning(informationItems.saddle, translation)))) // Llama interactions
                {
                    if (rawSlot == 30)
                        a(event, InventoryUtils.InformationItems.addWarning(informationItems.saddle, translation));
                    else if (rawSlot == 32)
                        a(event, informationItems.llamaDecor);
                }
            }
        }
    }

    private void a(InventoryClickEvent event, net.minecraft.server.v1_12_R1.ItemStack nmsInformationItem)
    {
        event.setCancelled(true);

        if (event.getCurrentItem().equals(CraftItemStack.asBukkitCopy(nmsInformationItem)))
        {
            if (event.getCursor() != null && event.getCursor().getType() != Material.AIR)
            {
                event.setCurrentItem(event.getCursor());
                event.setCursor(null);
            }
        }
        else
        {
            if (event.getCursor() != null && event.getCursor().getType() != Material.AIR)
            {
                ItemStack oldCursorItem = event.getCursor();

                event.setCursor(event.getCurrentItem());
                event.setCurrentItem(oldCursorItem);
            }
            else
            {
                event.setCursor(event.getCurrentItem());
                event.setCurrentItem(null);
            }
        }
    }
}