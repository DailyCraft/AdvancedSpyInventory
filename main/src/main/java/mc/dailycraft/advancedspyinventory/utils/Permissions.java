package mc.dailycraft.advancedspyinventory.utils;

import mc.dailycraft.advancedspyinventory.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.*;
import java.util.stream.Collectors;

public enum Permissions {
    INVENTORY,

    PLAYER_VIEW(INVENTORY),
    PLAYER_MODIFY(PLAYER_VIEW),
    PLAYER_SLOT(PLAYER_VIEW),
    PLAYER_DROP(PLAYER_MODIFY),
    PLAYER_HEALTH(PLAYER_VIEW),
    PLAYER_HEALTH_MODIFY(PLAYER_HEALTH),
    PLAYER_HEALTH_MODIFY_MAX(PLAYER_HEALTH_MODIFY),
    PLAYER_LOCATION(PLAYER_VIEW),
    PLAYER_TELEPORT(PLAYER_LOCATION),
    PLAYER_TELEPORT_OTHERS(PLAYER_TELEPORT),
    PLAYER_XP(PLAYER_VIEW),
    PLAYER_XP_MODIFY(PLAYER_XP),
    PLAYER_FOOD(PLAYER_VIEW),
    PLAYER_FOOD_MODIFY(PLAYER_FOOD),

    ENTITY_VIEW(INVENTORY),
    ENTITY_MODIFY(ENTITY_VIEW),
    ENTITY_HEALTH(ENTITY_VIEW),
    ENTITY_HEALTH_MODIFY(ENTITY_HEALTH),
    ENTITY_HEALTH_MODIFY_MAX(ENTITY_HEALTH_MODIFY),
    ENTITY_LOCATION(ENTITY_VIEW),
    ENTITY_TELEPORT(ENTITY_LOCATION),
    ENTITY_TELEPORT_OTHERS(ENTITY_TELEPORT),
    ENTITY_TAMED(ENTITY_VIEW),

    ENDER,
    ENDER_MODIFY(ENDER),
    ENDER_OTHERS(ENDER),
    ENDER_OTHERS_MODIFY(ENDER_OTHERS, ENDER_MODIFY),
    ;

    private static final Map<EntityType, Permission>
            ENTITY_INFORMATION = new HashMap<>(),
            ENTITY_INFORMATION_MODIFY = new HashMap<>();

    private final Permission permission;

    Permissions(Permissions... children) {
        String key = "inventory.";
        int i = -1;

        if (name().equals("INVENTORY"))
            key = "inventory";
        else if (name().startsWith("PLAYER")) {
            key += "player.";
            i = 7;
        } else if (name().startsWith("ENTITY")) {
            key += "entity.";
            i = 7;
        } else if (name().equals("ENDER"))
            key = "enderchest";
        else if (name().startsWith("ENDER")) {
            key = "enderchest.";
            i = 6;
        }

        if (i != -1)
            key += name().toLowerCase().substring(i).replace('_', '.');

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

        List<EntityType> types = new ArrayList<>();

        types.add(EntityType.BAT);
        types.add(EntityType.CREEPER);
        types.add(EntityType.IRON_GOLEM);
        types.add(EntityType.LLAMA);
        types.add(EntityType.OCELOT);
        types.add(EntityType.PIG);
        types.add(EntityType.RABBIT);
        types.add(EntityType.SHEEP);
        types.add(EntityType.SLIME);
        types.add(Main.VERSION >= 20.5 ? EntityType.SNOW_GOLEM : EntityType.valueOf("SNOWMAN"));
        types.add(EntityType.VILLAGER);
        types.add(EntityType.WOLF);
        if (Main.VERSION >= 13)
            types.add(EntityType.PHANTOM);
        if (Main.VERSION >= 14) {
            types.add(EntityType.CAT);
            types.add(EntityType.FOX);
            types.add(Main.VERSION >= 20.5 ? EntityType.MOOSHROOM : EntityType.valueOf("MUSHROOM_COW"));
            types.add(EntityType.PANDA);
            types.add(EntityType.TRADER_LLAMA);
        }
        if (Main.VERSION >= 15)
            types.add(EntityType.BEE);
        if (Main.VERSION >= 16)
            types.add(EntityType.STRIDER);
        if (Main.VERSION >= 17) {
            types.add(EntityType.AXOLOTL);
            types.add(EntityType.GOAT);
        }
        if (Main.VERSION >= 19)
            types.add(EntityType.ALLAY);

        for (EntityType type : types) {
            String key = start + "." + (Main.VERSION > 13 ? type.getKey().getKey() : type.getName());
            Permission perm = new Permission(key, Translation.of().format("permission.inventory.entity.information.specific", Main.VERSION > 13 ? type.getKey() : type.getName()), PermissionDefault.OP, valToMap(ENTITY_VIEW.get()));
            Bukkit.getPluginManager().addPermission(perm);
            ENTITY_INFORMATION.put(type, perm);

            key += ".modify";
            perm = new Permission(key, Translation.of().format("permission.inventory.entity.information.specific.modify", Main.VERSION > 13 ? type.getKey().getKey() : type.getName()), PermissionDefault.OP, valToMap(perm));
            Bukkit.getPluginManager().addPermission(perm);
            ENTITY_INFORMATION_MODIFY.put(type, perm);
        }

        Bukkit.getPluginManager().addPermission(new Permission(start, Translation.of().format("permission.inventory.entity.information"), PermissionDefault.OP, ENTITY_INFORMATION.values().stream().collect(Collectors.toMap(Permission::getName, perm -> true))));
        Bukkit.getPluginManager().addPermission(new Permission(start + ".modify", Translation.of().format("permission.inventory.entity.information.modify"), PermissionDefault.OP, ENTITY_INFORMATION_MODIFY.values().stream().collect(Collectors.toMap(Permission::getName, perm -> true))));
    }

    private static Map<String, Boolean> valToMap(Permission perm) {
        Map<String, Boolean> map = new HashMap<>();
        map.put(perm.getName(), true);
        return map;
    }

    public static boolean hasPermission(EntityType type, Permissible permissible) {
        return permissible.hasPermission(ENTITY_INFORMATION.get(type));
    }

    public static boolean hasPermissionModify(EntityType type, Permissible permissible) {
        return permissible.hasPermission(ENTITY_INFORMATION_MODIFY.get(type));
    }

    public static boolean hasPermissionModify(EntityType type, Permissible permissible, Entity entity) {
        return entity.getType() == type && hasPermissionModify(type, permissible);
    }
}