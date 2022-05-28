package mc.dailycraft.advancedspyinventory.utils;

import mc.dailycraft.advancedspyinventory.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public enum Permissions {
    INVENTORY("inventory"),

    PLAYER_VIEW("inventory.player.view", INVENTORY),
    PLAYER_MODIFY("inventory.player.modify", PLAYER_VIEW),
    PLAYER_SLOT("inventory.player.slot", PLAYER_VIEW),
    PLAYER_DROP("inventory.player.drop", PLAYER_MODIFY),
    PLAYER_HEALTH("inventory.player.health", PLAYER_VIEW),
    PLAYER_HEALTH_MODIFY("inventory.player.health.modify", PLAYER_HEALTH),
    PLAYER_HEALTH_MODIFY_MAX("inventory.player.health.modify.max", PLAYER_HEALTH_MODIFY),
    PLAYER_LOCATION("inventory.player.location", PLAYER_VIEW),
    PLAYER_TELEPORT("inventory.player.teleport", PLAYER_LOCATION),
    PLAYER_XP("inventory.player.xp", PLAYER_VIEW),
    PLAYER_XP_MODIFY("inventory.player.xp.modify", PLAYER_XP),
    PLAYER_FOOD("inventory.player.food", PLAYER_VIEW),
    PLAYER_FOOD_MODIFY("inventory.player.food.modify", PLAYER_FOOD),

    ENTITY_VIEW("inventory.entity.view", INVENTORY),
    ENTITY_MODIFY("inventory.entity.modify", ENTITY_VIEW),
    ENTITY_HEALTH("inventory.entity.health", ENTITY_VIEW),
    ENTITY_HEALTH_MODIFY("inventory.entity.health.modify", ENTITY_HEALTH),
    ENTITY_HEALTH_MODIFY_MAX("inventory.entity.health.modify.max", ENTITY_HEALTH_MODIFY),
    ENTITY_LOCATION("inventory.entity.location", ENTITY_VIEW),
    ENTITY_TELEPORT("inventory.entity.teleport", ENTITY_LOCATION),
    ENTITY_TAMED("inventory.entity.tamed", ENTITY_VIEW),

    ENDER("enderchest"),
    ENDER_MODIFY("enderchest.modify", ENDER),
    ENDER_OTHERS("enderchest.others", ENDER),
    ENDER_OTHERS_MODIFY("enderchest.others.modify", ENDER_OTHERS, ENDER_MODIFY),
    ;

    public static final Map<EntityType, Permission> ENTITY_INFORMATION = new HashMap<>(), ENTITY_INFORMATION_MODIFY = new HashMap<>();

    private final Permission permission;

    Permissions(String key, Permissions... children) {
        permission = new Permission(Main.getInstance().getName().toLowerCase() + "." + key,
                Translation.of().format("permission." + key), PermissionDefault.OP,
                Arrays.stream(children).collect(Collectors.toMap(permission -> permission.get().getName(), permission -> true)));
    }

    public Permission get() {
        return permission;
    }

    public boolean has(Permissible permissible) {
        return permissible.hasPermission(permission);
    }

    public static void init() {
        for (Permissions perm : Permissions.values())
            Bukkit.getPluginManager().addPermission(perm.get());

        String start = Main.getInstance().getName().toLowerCase() + ".inventory.entity.information";

        Arrays.asList(EntityType.SHEEP, EntityType.IRON_GOLEM, EntityType.FOX, EntityType.PANDA, EntityType.SLIME, EntityType.SNOWMAN, EntityType.VILLAGER, EntityType.LLAMA).forEach(entity -> {
            String key = start + "." + entity.getKey().getKey();
            Permission perm = new Permission(key, Translation.of().format("permission.inventory.entity.information.specific", entity.getKey()), PermissionDefault.OP, valToMap(ENTITY_VIEW.get()));
            Bukkit.getPluginManager().addPermission(perm);
            ENTITY_INFORMATION.put(entity, perm);

            key += ".modify";
            Permission permModify = new Permission(key, Translation.of().format("permission.inventory.entity.information.specific.modify", entity.getKey().getKey()), PermissionDefault.OP, valToMap(perm));
            Bukkit.getPluginManager().addPermission(permModify);
            ENTITY_INFORMATION_MODIFY.put(entity, permModify);
        });

        Bukkit.getPluginManager().addPermission(new Permission(start, Translation.of().format("permission.inventory.entity.information"), PermissionDefault.OP, ENTITY_INFORMATION.values().stream().collect(Collectors.toMap(Permission::getName, perm -> true))));
        Bukkit.getPluginManager().addPermission(new Permission(start + ".modify", Translation.of().format("permission.inventory.entity.information.modify"), PermissionDefault.OP, ENTITY_INFORMATION_MODIFY.values().stream().collect(Collectors.toMap(Permission::getName, perm -> true))));
    }

    private static Map<String, Boolean> valToMap(Permission perm) {
        Map<String, Boolean> map = new HashMap<>();
        map.put(perm.getName(), true);
        return map;
    }
}