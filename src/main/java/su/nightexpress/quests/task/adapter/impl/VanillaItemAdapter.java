package su.nightexpress.quests.task.adapter.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.LangUtil;
import su.nightexpress.quests.task.adapter.type.BukkitAdapter;

public class VanillaItemAdapter extends BukkitAdapter<Material, ItemStack> {

    public VanillaItemAdapter(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean canHandle(@NotNull ItemStack itemStack) {
        return true;
    }

    @Override
    @Nullable
    public Material getTypeByName(@NotNull String name) {
        Material material = BukkitThing.getMaterial(name);
        return material != null && material.isItem() ? material : null;
    }

    @Override
    @Nullable
    public Material getType(@NotNull ItemStack itemStack) {
        return itemStack.getType();
    }

    @Override
    @Nullable
    public String getLocalizedName(@NotNull Material type) {
        return LangUtil.getSerializedName(type);
    }
}
