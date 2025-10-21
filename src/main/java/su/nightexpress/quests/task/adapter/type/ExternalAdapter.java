package su.nightexpress.quests.task.adapter.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.bridge.common.NightKey;

public abstract class ExternalAdapter<I, O> extends AbstractAdapter<I, O> {

    protected final String namespace;

    public ExternalAdapter(@NotNull String name, @NotNull String namespace) {
        super(name);
        this.namespace = namespace;
    }

    @NotNull
    public String getNamespace() {
        return this.namespace;
    }

    public boolean isNamespace(@NotNull String name) {
        return this.namespace.equalsIgnoreCase(name);
    }

    @Override
    public boolean canHandle(@NotNull String fullName) {
        return NightKey.key(fullName).namespace().equalsIgnoreCase(this.namespace);
    }

    @Override
    @Nullable
    public String getLocalizedName(@NotNull String fullName) {
        NightKey key = NightKey.key(fullName);
        if (!this.isNamespace(key.namespace())) return null;

        I type = this.getTypeByName(key.value());
        return type == null ? null : this.getLocalizedName(type);
    }

    @NotNull
    public String toFullNameOfType(@NotNull I type) {
        String typeName = this.getTypeName(type);
        return NightKey.key(this.namespace, typeName).asString();
    }
}
