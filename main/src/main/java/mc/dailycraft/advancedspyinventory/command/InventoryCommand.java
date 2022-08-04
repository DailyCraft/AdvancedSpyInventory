package mc.dailycraft.advancedspyinventory.command;

import mc.dailycraft.advancedspyinventory.Main;
import mc.dailycraft.advancedspyinventory.inventory.*;
import mc.dailycraft.advancedspyinventory.inventory.entity.*;
import mc.dailycraft.advancedspyinventory.utils.Permissions;
import mc.dailycraft.advancedspyinventory.utils.Translation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class InventoryCommand extends PlayerTabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Translation translation = Translation.of(player);

            if (args.length == 1) {
                UUID targetUuid = null;

                if (Main.VERSION >= 13) {
                    try {
                        List<Entity> entities = Bukkit.selectEntities(sender, args[0]);

                        if (entities.size() == 1)
                            targetUuid = entities.get(0).getUniqueId();
                        else if (entities.size() > 1) {
                            sender.sendMessage(translation.format("command.inventory.only_one"));
                            return true;
                        }
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                if (targetUuid == null) {
                    try {
                        targetUuid = UUID.fromString(args[0]);
                    } catch (IllegalArgumentException exception) {
                        targetUuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
                    }
                }

                Entity targetEntity = Bukkit.getEntity(targetUuid);

                if (targetEntity == null || targetEntity.getType() == EntityType.PLAYER) {
                    if (Permissions.PLAYER_VIEW.has(player)) {
                        if (Bukkit.getOfflinePlayer(targetUuid).isOnline() || Bukkit.getOfflinePlayer(targetUuid).hasPlayedBefore())
                            new PlayerInventory(player, targetUuid).getView().open();
                        else
                            player.sendMessage(translation.format("command.never_connected"));
                    } else
                        player.sendMessage(translation.format("command.inventory.player"));
                } else if (targetEntity instanceof LivingEntity) {
                    if (Permissions.ENTITY_VIEW.has(player)) {
                        if (targetEntity.getType() == EntityType.ENDERMAN)
                            new EndermanInventory(player, (Enderman) targetEntity).getView().open();
                        else if (targetEntity.getType() == EntityType.HORSE || targetEntity.getType() == EntityType.ZOMBIE_HORSE || targetEntity.getType() == EntityType.SKELETON_HORSE)
                            new HorseInventory<>(player, (AbstractHorse) targetEntity).getView().open();
                        else if (targetEntity.getType() == EntityType.DONKEY || targetEntity.getType() == EntityType.MULE)
                            new DonkeyInventory(player, (ChestedHorse) targetEntity).getView().open();
                        else if (targetEntity.getType() == EntityType.LLAMA || (Main.VERSION >= 14 && targetEntity.getType() == EntityType.TRADER_LLAMA))
                            new LlamaInventory(player, (Llama) targetEntity).getView().open();
                        else
                            new EntityInventory<>(player, (LivingEntity) targetEntity).getView().open();
                    } else
                        player.sendMessage(translation.format("command.inventory.entity"));
                }
            } else
                sender.sendMessage(ChatColor.RED + command.getUsage().replace("<command>", command.getLabel()));
        } else
            sender.sendMessage(Translation.of().format("command.not_player"));

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player && args.length == 1) {
            List<String> list = super.onTabComplete(sender, command, label, args);
            if (list == null)
                list = new ArrayList<>();

            if (Permissions.ENTITY_VIEW.has(sender)) {
                Player player = (Player) sender;
                Entity target = null;

                if (Main.VERSION < 15) {
                    for (Entity other : player.getNearbyEntities(6, 6, 6)) {
                        Vector n = other.getLocation().toVector().subtract(player.getLocation().toVector());
                        if (player.getLocation().getDirection().normalize().crossProduct(n).lengthSquared() < 1 && n.normalize().dot(player.getLocation().getDirection().normalize()) >= 0)
                            if (target == null || target.getLocation().distanceSquared(player.getLocation()) > other.getLocation().distanceSquared(player.getLocation()))
                                target = other;
                    }
                } else {
                    RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getLocation().getDirection(), 6, FluidCollisionMode.ALWAYS, false, 0, entity -> entity != player);

                    if (rayTrace != null)
                        target = rayTrace.getHitEntity();
                }

                if (target != null && player.hasLineOfSight(target) && target.getUniqueId().toString().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    list.add(target.getUniqueId().toString());
            }

            return list;
        }

        return Collections.emptyList();
    }
}