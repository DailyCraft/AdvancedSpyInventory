package minecraft.dailycraft.advancedspyinventory.utils;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashMap;
import java.util.Map;

public enum Permissions
{
    INVENTORY("inventory", "Allows you to execute the /inventory command"),
    INVENTORY_MODIFY("inventory.modify", "Allows you to modify player inventory", INVENTORY),
    INVENTORY_CLEAR("inventory.clear", "Allows you to clear player inventory", INVENTORY_MODIFY),
    INVENTORY_LOCATION("inventory.location", "Allows you to see the player's location", INVENTORY),
    INVENTORY_LOCATION_TELEPORT("inventory.location.teleport", "Allows you to teleport to player location", INVENTORY_LOCATION),

    ENDER("enderchest", "Allows you to execute the /enderchest command"),
    ENDER_MODIFY("enderchest.modify", "Allows you to modify your own ender chest", ENDER),
    ENDER_OTHERS("enderchest.others", "Allows you to open the ender chest of other players", ENDER),
    ENDER_OTHERS_MODIFY("enderchest.others.modify", "Allows you to modify the ender chest of other players", ENDER_OTHERS),
    ENDER_CLEAR("enderchest.clear", "Allows you to clear the ender chests", ENDER_MODIFY),

    ENTITY("entityinventory", "Allows you to see the inventory of entities"),
    ENTITY_MODIFY("entityinventory.modify", "Allows you to modify the inventory of entities", ENTITY),
    ;

    private final String key;
    private final String description;
    private final PermissionDefault defaultValue;
    private final Permissions[] children;

    Permissions(String key, String description, PermissionDefault defaultValue, Permissions... children)
    {
        this.key = "advancedspyinventory." + key;
        this.description = description;
        this.defaultValue = defaultValue;
        this.children = children;
    }

    Permissions(String key, String description, Permissions... children)
    {
        this(key, description, PermissionDefault.OP, children);
    }

    public Permission get()
    {
        Map<String, Boolean> children = new HashMap<>();

        for (Permissions perm : this.children)
            children.put(perm.get().getName(), true);

        return new Permission(key, description, defaultValue, children);
    }
}