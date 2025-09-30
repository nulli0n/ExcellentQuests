package su.nightexpress.quests.task.adapter.impl;

import net.momirealms.customfishing.api.BukkitCustomFishingPlugin;
import net.momirealms.customfishing.api.mechanic.loot.Loot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.quests.task.adapter.type.ExternalAdapter;

public class CustomFishingAdapter extends ExternalAdapter<Loot, ItemStack> {

    public CustomFishingAdapter(@NotNull String name) {
        super(name, "customfishing");
    }

    @NotNull
    private static BukkitCustomFishingPlugin getAPI() {
        return BukkitCustomFishingPlugin.getInstance();
    }

    @Override
    public boolean canHandle(@NotNull ItemStack itemStack) {
        return getAPI().getItemManager().getCustomFishingItemID(itemStack) != null;
    }

    @Override
    @Nullable
    public Loot getTypeByName(@NotNull String name) {
        return getAPI().getLootManager().getRegisteredLoots().stream().filter(loot -> loot.id().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    @Nullable
    public Loot getType(@NotNull ItemStack itemStack) {
        String id = getAPI().getItemManager().getCustomFishingItemID(itemStack);
        if (id == null) return null;

        return getTypeByName(id);
    }

    @Override
    @NotNull
    public String getTypeName(@NotNull Loot loot) {
        return loot.id();
    }

    @Override
    @Nullable
    public String getLocalizedName(@NotNull Loot loot) {
        return loot.nick();
    }
}
