package su.nightexpress.quests.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.LowerCase;

import java.util.*;

public class NightRegistry<T> {

    private final Map<String, T> byKey;

    private boolean frozen;

    public NightRegistry(@NotNull Map<String, T> byKey) {
        this.byKey = byKey;
    }

    @NotNull
    public static <E> NightRegistry<E> create() {
        return new NightRegistry<>(new HashMap<>());
    }

    @NotNull
    public static <E> NightRegistry<E> createOrdered() {
        return new NightRegistry<>(new LinkedHashMap<>());
    }

    public void add(@NotNull String key, @NotNull T value) {
        if (this.isFrozen()) throw new IllegalStateException("Could not add values to frozen registry!");

        this.byKey.put(LowerCase.INTERNAL.apply(key), value);
    }

    @Nullable
    public T byKey(@NotNull String key) {
        return this.byKey.get(LowerCase.INTERNAL.apply(key));
    }

    @NotNull
    public Set<T> values() {
        return new HashSet<>(this.byKey.values());
    }

    public int size() {
        return this.byKey.size();
    }

    public void clear() {
        this.byKey.clear();
    }

    public void freeze() {
        this.frozen = true;
    }

    public void unfreeze() {
        this.frozen = false;
    }

    public boolean isFrozen() {
        return this.frozen;
    }
}
