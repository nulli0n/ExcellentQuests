package su.nightexpress.quests.task.adapter.impl;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.quests.task.adapter.type.ExternalAdapter;

public class MythicMobAdapter extends ExternalAdapter<MythicMob, Entity> {

    public MythicMobAdapter(@NotNull String name) {
        super(name, "mythicmobs");
    }

    private static MythicBukkit getAPI() {
        return MythicBukkit.inst();
    }

    @Override
    public boolean canHandle(@NotNull Entity entity) {
        return getAPI().getMobManager().isMythicMob(entity);
    }

    @Override
    @Nullable
    public MythicMob getTypeByName(@NotNull String name) {
        return getAPI().getMobManager().getMythicMob(name).orElse(null);
    }

    @Override
    @Nullable
    public MythicMob getType(@NotNull Entity entity) {
        ActiveMob activeMob = getAPI().getMobManager().getMythicMobInstance(entity);
        if (activeMob == null) return null;

        return activeMob.getType();
    }

    @Override
    @NotNull
    public String getTypeName(@NotNull MythicMob type) {
        return type.getInternalName();
    }

    @Override
    @Nullable
    public String getLocalizedName(@NotNull MythicMob mythicMob) {
        return mythicMob.getDisplayName().get();
    }
}
