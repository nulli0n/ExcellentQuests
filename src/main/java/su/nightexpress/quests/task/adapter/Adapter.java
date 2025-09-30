package su.nightexpress.quests.task.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.quests.task.adapter.impl.*;

public interface Adapter<I, O> {

    VanillaMobAdapter         VANILLA_MOB         = new VanillaMobAdapter("vanilla_mob");
    VanillaBlockAdapter       VANILLA_BLOCK       = new VanillaBlockAdapter("vanilla_block");
    VanillaBlockStateAdapter  VANILLA_BLOCK_STATE = new VanillaBlockStateAdapter("vanilla_block_state");
    VanillaItemAdapter        VANILLA_ITEM        = new VanillaItemAdapter("vanilla_item");
    VanillaEnchantmentAdapter VANILLA_ENCHANTMENT = new VanillaEnchantmentAdapter("enchantment");

    boolean canHandle(@NotNull O entity);

    boolean canHandle(@NotNull String fullName);

    @NotNull String getName();

    @Nullable I getTypeByName(@NotNull String name);

    @Nullable I getType(@NotNull O entity);

    @NotNull String getTypeName(@NotNull I type);



    @Nullable String getLocalizedName(@NotNull String fullName);

    @Nullable String getLocalizedName(@NotNull I type);



    @Nullable String toFullNameOfEntity(@NotNull O entity);

    @NotNull String toFullNameOfType(@NotNull I type);

    @NotNull String toFullName(@NotNull String name);
}
