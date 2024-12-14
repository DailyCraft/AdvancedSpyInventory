package mc.dailycraft.advancedspyinventory.utils;

import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ClassChange {
    private static final Map<Class<?>, Method> enumName = new HashMap<>();
    public static String enumName(Object oldEnum) {
        try {
            if (!enumName.containsKey(oldEnum.getClass()))
                enumName.put(oldEnum.getClass(), oldEnum.getClass().getMethod("name"));
            return (String) enumName.get(oldEnum.getClass()).invoke(oldEnum);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static final Map<Class<?>, Method> enumOrdinal = new HashMap<>();
    public static int enumOrdinal(Object oldEnum) {
        try {
            if (!enumOrdinal.containsKey(oldEnum.getClass()))
                enumOrdinal.put(oldEnum.getClass(), oldEnum.getClass().getMethod("ordinal"));
            return (int) enumOrdinal.get(oldEnum.getClass()).invoke(oldEnum);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static final Map<Class<?>, Method> enumValues = new HashMap<>();
    public static <T> T[] enumValues(Class<T> oldEnum) {
        try {
            if (!enumValues.containsKey(oldEnum))
                enumValues.put(oldEnum, oldEnum.getMethod("values"));
            return (T[]) enumValues.get(oldEnum).invoke(null);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static final Map<Class<?>, Method> enumValueOf = new HashMap<>();
    public static <T> T enumValueOf(Class<T> oldEnum, String key) {
        try {
            if (!enumValueOf.containsKey(oldEnum))
                enumValueOf.put(oldEnum, oldEnum.getMethod("valueOf", String.class));
            return (T) enumValueOf.get(oldEnum).invoke(null, key);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Method getClass;
    public static Class<?> getClass(Object object) {
        try {
            if (getClass == null)
                getClass = object.getClass().getMethod("getClass");
            return (Class<?>) getClass.invoke(object);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Method setCursor;
    public static void setCursor(InventoryView view, ItemStack stack) {
        try {
            if (setCursor == null)
                setCursor = InventoryView.class.getMethod("setCursor", ItemStack.class);
            setCursor.invoke(view, stack);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }
}