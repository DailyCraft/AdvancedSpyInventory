package minecraft.dailycraft.advancedspyinventory.command;

import minecraft.dailycraft.advancedspyinventory.inventory.Inventories;
import minecraft.dailycraft.advancedspyinventory.utils.Permissions;
import minecraft.dailycraft.advancedspyinventory.utils.TranslationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class InventoryCommand extends PlayerTabExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            TranslationUtils translation = new TranslationUtils(player);

            if (args.length == 1)
            {
                UUID targetUuid;

                try
                {
                    targetUuid = UUID.fromString(args[0]);
                }
                catch (IllegalArgumentException exception)
                {
                    targetUuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
                }

                Entity targetEntity = Bukkit.getEntity(targetUuid);

                if (targetEntity == null || targetEntity instanceof Player)
                {
                    if (Bukkit.getOfflinePlayer(targetUuid).isOnline() || Bukkit.getOfflinePlayer(targetUuid).hasPlayedBefore())
                    {
                        player.openInventory(Inventories.getPlayerInventory(player, targetUuid));
                    }
                    else
                    {
                        sender.sendMessage(translation.format("command.error.played_before"));
                    }
                }
                else if (targetEntity instanceof LivingEntity)
                {
                    if (sender.hasPermission(Permissions.ENTITY.get()))
                    {
                        if (targetEntity.getType() == EntityType.VILLAGER)
                            player.openInventory(Inventories.getVillagerInventory(player, (Villager) targetEntity));
                        else if (targetEntity.getType() == EntityType.ENDERMAN)
                            player.openInventory(Inventories.getEndermanInventory(player, (Enderman) targetEntity));
                        else if (targetEntity.getType() == EntityType.SHEEP)
                            player.openInventory(Inventories.getSheepInventory(player, (Sheep) targetEntity));
                        else if (targetEntity.getType() == EntityType.HORSE || targetEntity.getType() == EntityType.ZOMBIE_HORSE || targetEntity.getType() == EntityType.SKELETON_HORSE)
                            player.openInventory(Inventories.getHorseInventory(player, (AbstractHorse) targetEntity));
                        else if (targetEntity.getType() == EntityType.DONKEY || targetEntity.getType() == EntityType.MULE)
                            player.openInventory(Inventories.getDonkeyAndMuleInventory(player, (ChestedHorse) targetEntity));
                        else if (targetEntity.getType() == EntityType.LLAMA)
                            player.openInventory(Inventories.getLlamaInventory(player, (Llama) targetEntity));
                        else
                            player.openInventory(Inventories.getEntityInventory(player, (LivingEntity) targetEntity));
                    }
                    else
                    {
                        sender.sendMessage(translation.format("permission.entityinventory"));
                    }
                }
            }
            else
            {
                sender.sendMessage(ChatColor.RED + command.getUsage().replace("<command>", command.getLabel()));
            }
        }
        else
        {
            sender.sendMessage(new TranslationUtils(null).format("command.error.console"));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        if (sender instanceof Player)
        {
            if (args.length == 1)
            {
                Player player = (Player) sender;
                Entity targetEntity = null;

                List<Entity> nearbyEntities = player.getNearbyEntities(50, 50, 50);

                ArrayList<LivingEntity> livingEntities = new ArrayList<>();

                nearbyEntities.forEach(entity ->
                {
                    if (entity instanceof LivingEntity)
                        livingEntities.add((LivingEntity) entity);
                });

                BlockIterator blockIterator = new BlockIterator(player, 50);

                Block block;
                Location location;
                int blockX, blockY, blockZ;
                double entityX, entityY, entityZ;

                while (blockIterator.hasNext())
                {
                    block = blockIterator.next();

                    blockX = block.getX();
                    blockY = block.getY();
                    blockZ = block.getZ();

                    for (LivingEntity entity : livingEntities)
                    {
                        location = entity.getLocation();

                        entityX = location.getX();
                        entityY = location.getY();
                        entityZ = location.getZ();

                        if ((blockX - 0.75 <= entityX && entityX <= blockX + 1.75) && (blockY - 1 <= entityY && entityY <= blockY + 2.5) && (blockZ - 0.75 <= entityZ && entityZ <= blockZ + 1.75))
                        {
                            targetEntity = entity;
                            break;
                        }
                    }
                }

                List<String> list = super.onTabComplete(sender, command, alias, args);

                try
                {
                    if (targetEntity.getUniqueId().toString().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                        list.add(targetEntity.getUniqueId().toString());
                }
                catch (NullPointerException ignored)
                {}

                return list;
            }
        }

        return Collections.emptyList();
    }
}