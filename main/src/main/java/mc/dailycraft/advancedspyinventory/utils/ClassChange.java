package mc.dailycraft.advancedspyinventory.utils;

import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ClassChange {
    private static Method enumName;
    public static String enumName(Object oldEnum) {
        if (enumName == null) {
            try {
                enumName = oldEnum.getClass().getMethod("name");
            } catch (NoSuchMethodException exception) {
                throw new RuntimeException(exception);
            }
        }

        try {
            return (String) enumName.invoke(oldEnum);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Method enumOrdinal;
    public static int enumOrdinal(Object oldEnum) {
        if (enumOrdinal == null) {
            try {
                enumOrdinal = oldEnum.getClass().getMethod("ordinal");
            } catch (NoSuchMethodException exception) {
                throw new RuntimeException(exception);
            }
        }

        try {
            return (int) enumOrdinal.invoke(oldEnum);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static final Map<Class<?>, Method> enumValues = new HashMap<>();
    public static <T> T[] enumValues(Class<T> oldEnum) {
        if (!enumValues.containsKey(oldEnum)) {
            try {
                enumValues.put(oldEnum, oldEnum.getMethod("values"));
            } catch (NoSuchMethodException exception) {
                throw new RuntimeException(exception);
            }
        }

        try {
            return (T[]) enumValues.get(oldEnum).invoke(null);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Method getClass;
    public static Class<?> getClass(Object object) {
        if (getClass == null) {
            try {
                getClass = object.getClass().getMethod("getClass");
            } catch (NoSuchMethodException exception) {
                throw new RuntimeException(exception);
            }
        }

        try {
            return (Class<?>) getClass.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Method setCursor;
    public static void setCursor(InventoryView view, ItemStack stack) {
        if (setCursor == null) {
            try {
                setCursor = InventoryView.class.getMethod("setCursor", ItemStack.class);
            } catch (NoSuchMethodException exception) {
                throw new RuntimeException(exception);
            }
        }

        try {
            setCursor.invoke(view, stack);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }
}