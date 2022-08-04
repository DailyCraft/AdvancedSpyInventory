package mc.dailycraft.advancedspyinventory.utils;

import mc.dailycraft.advancedspyinventory.inventory.entity.EntityInventory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.naming.NoPermissionException;
import java.util.function.BiFunction;

public class DataItem<T extends LivingEntity> {
    private final BiFunction<EntityInventory<T>, T, ItemStack> get;
    private final TriConsumer<EntityInventory<T>, InventoryClickEvent, T> click;

    public DataItem(BiFunction<EntityInventory<T>, T, ItemStack> get, TriConsumer<EntityInventory<T>, InventoryClickEvent, T> click) {
        this.get = get;
        this.click = click;
    }

    public ItemStack get(EntityInventory<T> inventory, EntityType type, Player viewer) throws NoPermissionException {
        if (Permissions.hasPermission(type, viewer))
            return get.apply(inventory, inventory.entity);

        throw new NoPermissionException();
    }

    public void click(EntityInventory<T> inventory, InventoryClickEvent event, EntityType type, Player viewer) {
        if (Permissions.hasPermissionModify(type, viewer) && click != null)
            click.accept(inventory, event, inventory.entity);
    }

    @FunctionalInterface
    public interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }
}