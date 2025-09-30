package su.nightexpress.quests.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.quests.task.adapter.Adapter;
import su.nightexpress.quests.task.adapter.AdapterFamily;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractObjectiveTable<T> implements Writeable {

    protected final Map<String, T> entires;

    public AbstractObjectiveTable() {
        this(new HashMap<>());
    }

    public AbstractObjectiveTable(@NotNull Map<String, T> entires) {
        this.entires = entires;
    }

    /*@NotNull
    protected static <R, E extends AbstractObjectiveTable<R>> E read(@NotNull FileConfig config,
                                                                     @NotNull String path,
                                                                     @NotNull Function<String, R> function,
                                                                     @NotNull Function<Map<String, R>, E> construct) {
        Map<String, R> entires = new HashMap<>();

        config.getSection(path).forEach(name -> {
            String data = config.getString(path + "." + name);
            if (data == null) return;

            R objective = function.apply(data);
            entires.put(name, objective);
        });

        return construct.apply(entires);
    }*/

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.remove(path);
        this.entires.forEach((name, entry) -> {
            config.set(path + "." + name, entry);
        });
    }

    public <O> boolean contains(@NotNull O entity, @NotNull AdapterFamily<O> family) {
        return !this.getEntries(entity, family).isEmpty();
    }

    @NotNull
    public <O> Set<T> getEntries(@NotNull O entity, @NotNull AdapterFamily<O> family) {
        Set<T> entries = new HashSet<>();

        family.getAdaptersFor(entity).forEach(adapter -> {
            T entry = this.getEntryByEntity(entity, adapter);
            if (entry == null) return;

            entries.add(entry);
        });

        return entries;
    }

    @Nullable
    public <I, O, E extends Adapter<I, O>> T getEntryByEntity(@NotNull O entity, @NotNull E adapter) {
        String fullName = adapter.toFullNameOfEntity(entity);
        return fullName == null ? null : this.getEntry(fullName);
    }

    @Nullable
    public <I, O, E extends Adapter<I, O>> T getEntryByType(@NotNull I type, @NotNull E adapter) {
        return this.getEntry(adapter.toFullNameOfType(type));
    }

    @Nullable
    public <I, O, E extends Adapter<I, O>> T getEntry(@NotNull String fullName, @NotNull E adapter) {
        return this.getEntry(adapter.toFullName(fullName));
    }

    @Nullable
    public T getEntry(@NotNull String fullName) {
        return this.entires.get(fullName);
    }

    @NotNull
    public Map<String, T> getEntryMap() {
        return this.entires;
    }
}
