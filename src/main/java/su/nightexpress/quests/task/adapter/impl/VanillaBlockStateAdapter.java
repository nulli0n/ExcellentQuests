package su.nightexpress.quests.task.adapter.impl;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.LangUtil;
import su.nightexpress.quests.task.adapter.type.BukkitAdapter;

public class VanillaBlockStateAdapter extends BukkitAdapter<Material, BlockState> {

    public VanillaBlockStateAdapter(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean canHandle(@NotNull BlockState block) {
        return true;
    }

    @Override
    @Nullable
    public Material getTypeByName(@NotNull String name) {
        Material material = BukkitThing.getMaterial(name);
        return material != null && material.isBlock() ? material : null;
    }

    @Override
    @Nullable
    public Material getType(@NotNull BlockState blockState) {
        return blockState.getType();
    }

    @Override
    @Nullable
    public String getLocalizedName(@NotNull Material type) {
        return LangUtil.getSerializedName(type);
    }
}
