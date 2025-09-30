package su.nightexpress.quests.task.adapter.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.quests.task.adapter.Adapter;

public abstract class AbstractAdapter<I, O> implements Adapter<I, O> {

    protected final String name;

    public AbstractAdapter(@NotNull String name) {
        this.name = name;
    }

    @Override
    @NotNull
    public String getName() {
        return this.name;
    }

    @Nullable
    public String toFullNameOfEntity(@NotNull O entity) {
        I type = this.getType(entity);
        if (type == null) return null;

        return this.toFullNameOfType(type);
    }

    @NotNull
    public String toFullName(@NotNull String name) {
        I type = this.getTypeByName(name);
        return type == null ? name : this.toFullNameOfType(type);
    }
}
