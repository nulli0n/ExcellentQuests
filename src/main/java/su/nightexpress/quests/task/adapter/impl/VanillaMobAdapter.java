package su.nightexpress.quests.task.adapter.impl;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.LangUtil;
import su.nightexpress.quests.task.adapter.type.BukkitAdapter;

public class VanillaMobAdapter extends BukkitAdapter<EntityType, Entity> {

    public VanillaMobAdapter(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean canHandle(@NotNull Entity entity) {
        return true;
    }

    @Override
    @Nullable
    public EntityType getTypeByName(@NotNull String name) {
        return BukkitThing.getEntityType(name);
    }

    @Override
    @NotNull
    public EntityType getType(@NotNull Entity entity) {
        return entity.getType();
    }

    @Override
    @Nullable
    public String getLocalizedName(@NotNull EntityType type) {
        return LangUtil.getSerializedName(type);
    }
}
