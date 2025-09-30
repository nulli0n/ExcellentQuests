package su.nightexpress.quests.task.adapter.type;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.bridge.common.NightKey;
import su.nightexpress.nightcore.util.BukkitThing;

public abstract class BukkitAdapter<I extends Keyed, O> extends AbstractAdapter<I, O> {

    public BukkitAdapter(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean canHandle(@NotNull String fullName) {
        return NightKey.fromString(fullName).namespace().equalsIgnoreCase(NamespacedKey.MINECRAFT);
    }

    @Override
    @NotNull
    public String getTypeName(@NotNull I type) {
        return BukkitThing.getAsString(type);
    }

    @Override
    @NotNull
    public String toFullNameOfType(@NotNull I type) {
        return BukkitThing.getAsString(type);
    }

    @Override
    @Nullable
    public String getLocalizedName(@NotNull String fullName) {
        I type = this.getTypeByName(fullName);
        return type == null ? null : this.getLocalizedName(type);
    }
}
