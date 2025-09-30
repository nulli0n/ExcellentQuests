package su.nightexpress.quests.task.adapter.impl;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.LangUtil;
import su.nightexpress.quests.task.adapter.type.BukkitAdapter;

public class VanillaBlockAdapter extends BukkitAdapter<Material, Block> {

    public VanillaBlockAdapter(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean canHandle(@NotNull Block block) {
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
    public Material getType(@NotNull Block block) {
        return block.getType();
    }

    @Override
    @Nullable
    public String getLocalizedName(@NotNull Material type) {
        return LangUtil.getSerializedName(type);
    }
}
