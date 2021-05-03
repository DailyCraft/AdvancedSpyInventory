package minecraft.dailycraft.advancedspyinventory.inventory;

import minecraft.dailycraft.advancedspyinventory.utils.Config;
import minecraft.dailycraft.advancedspyinventory.utils.TranslationUtils;
import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InventoryUtils
{
    public static ItemStack setItemWithDisplayName(org.bukkit.inventory.ItemStack stack, String displayName, String... lore)
    {
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName("§r" + displayName);

        List<String> loreList = new ArrayList<>();

        for (String s : lore)
        {
            String[] words = s.split(" ");
            StringBuilder line = new StringBuilder();

            for (String word : words)
            {
                line.append(word).append(" ");

                if (line.toString().split(" ").length == 10)
                {
                    loreList.add((words[0].startsWith("§") ? words[0].substring(0, 2) : "") + line + " ");
                    line.delete(0, line.length() - 1);
                }
            }

            loreList.add((words[0].startsWith("§") ? words[0].substring(0, 2) : "") + line + " ");
        }

        meta.setLore(loreList);

        stack.setItemMeta(meta);

        return CraftItemStack.asNMSCopy(stack);
    }

    public static ItemStack setItemWithDisplayName(Material material, String displayName, String... lore)
    {
        return setItemWithDisplayName(new org.bukkit.inventory.ItemStack(material), displayName, lore);
    }

    public static ItemStack getVoidItem()
    {
        return setItemWithDisplayName(new org.bukkit.inventory.ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), "");
    }

    public static ItemStack setPotionWithDisplayName(PotionType type, String displayName, String... lore)
    {
        org.bukkit.inventory.ItemStack potion = new org.bukkit.inventory.ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        meta.setDisplayName("§r" + displayName);
        meta.setLore(Arrays.asList(lore));

        meta.setBasePotionData(new PotionData(type));
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        potion.setItemMeta(meta);

        return CraftItemStack.asNMSCopy(potion);
    }

    public static class OfflinePlayer
    {
        private final UUID uuid;

        public OfflinePlayer(UUID uuid)
        {
            this.uuid = uuid;
        }

        public boolean isOnline()
        {
            return Bukkit.getOfflinePlayer(uuid).isOnline();
        }

        public Player getOnlinePlayer()
        {
            return Bukkit.getPlayer(uuid);
        }

        public minecraft.dailycraft.advancedspyinventory.utils.OfflinePlayer getOfflinePlayer()
        {
            return (minecraft.dailycraft.advancedspyinventory.utils.OfflinePlayer) Config.get().get(uuid.toString());
        }

        public UUID getUuid()
        {
            return uuid;
        }
    }

    public static class InformationItems
    {
        public final ItemStack helmet;
        public final ItemStack chestplate;
        public final ItemStack leggings;
        public final ItemStack boots;
        public final ItemStack mainHand;
        public final ItemStack offHand;

        public final ItemStack saddle;
        public final ItemStack horseArmor;
        public final ItemStack llamaDecor;

        public InformationItems(TranslationUtils translation)
        {
            org.bukkit.inventory.ItemStack stack = CraftItemStack.asBukkitCopy(setItemWithDisplayName(new org.bukkit.inventory.ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 8), null));

            helmet = setItemWithDisplayName(stack, translation.format("item.helmet"), translation.format("item.description"));
            chestplate = setItemWithDisplayName(stack, translation.format("item.chestplate"), translation.format("item.description"));
            leggings = setItemWithDisplayName(stack, translation.format("item.leggings"), translation.format("item.description"));
            boots = setItemWithDisplayName(stack, translation.format("item.boots"), translation.format("item.description"));
            mainHand = setItemWithDisplayName(stack, translation.format("item.main_hand"), translation.format("item.description"));
            offHand = setItemWithDisplayName(stack, translation.format("item.off_hand"), translation.format("item.description"));

            saddle = setItemWithDisplayName(stack, translation.format("item.saddle"), translation.format("item.description"));
            horseArmor = setItemWithDisplayName(stack, translation.format("item.horse_armor"), translation.format("item.description"));
            llamaDecor = setItemWithDisplayName(stack, translation.format("item.llama_decor"), translation.format("item.description"));
        }

        public static ItemStack getItem(ItemStack currentStack, ItemStack informationItem)
        {
            return currentStack == null || currentStack.isEmpty() || CraftItemStack.asBukkitCopy(currentStack).getType() == Material.AIR ? informationItem : currentStack;
        }

        public static ItemStack addWarning(ItemStack informationItem, TranslationUtils translation)
        {
            org.bukkit.inventory.ItemStack bukkitItem = CraftItemStack.asBukkitCopy(informationItem);
            ItemMeta meta = bukkitItem.getItemMeta();

            List<String> loreList = new ArrayList<>(meta.getLore());
            loreList.add("");

            String[] words = translation.format("item.warning").split(" ");
            StringBuilder line = new StringBuilder();

            for (String word : words)
            {
                line.append(word).append(" ");

                if (line.toString().split(" ").length == 10)
                {
                    loreList.add((words[0].startsWith("§") ? words[0].substring(0, 2) : "") + line + " ");
                    line.delete(0, line.length() - 1);
                }
            }

            loreList.add((words[0].startsWith("§") ? words[0].substring(0, 2) : "") + line + " ");

            meta.setLore(loreList);

            bukkitItem.setItemMeta(meta);

            return CraftItemStack.asNMSCopy(bukkitItem);
        }

        public ItemStack[] items()
        {
            return new ItemStack[] {mainHand, offHand, boots, leggings, chestplate, helmet};
        }
    }
}