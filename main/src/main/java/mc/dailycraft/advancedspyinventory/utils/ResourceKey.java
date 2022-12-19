package mc.dailycraft.advancedspyinventory.utils;

import org.bukkit.NamespacedKey;

import java.util.function.Function;

public class ResourceKey {
    private final String namespace, key;

    public ResourceKey(String namespace, String key) {
        this.namespace = namespace;
        this.key = key;
    }

    // Only for 1.12+
    public ResourceKey(NamespacedKey bukkitKey) {
        this(bukkitKey.getNamespace(), bukkitKey.getKey());
    }

    public String getNamespace() {
        return namespace;
    }

    public String getKey() {
        return key;
    }

    public static <T> ResourceKey fromOther(T otherKey, Function<T, String> namespace, Function<T, String> key) {
        return new ResourceKey(namespace.apply(otherKey), key.apply(otherKey));
    }

    public static ResourceKey minecraft(String key) {
        return new ResourceKey("minecraft", key);
    }

    @Override
    public String toString() {
        return getNamespace() + ":" + getKey();
    }
}