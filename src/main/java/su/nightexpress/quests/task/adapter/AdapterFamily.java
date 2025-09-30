package su.nightexpress.quests.task.adapter;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AdapterFamily<O> {

    public static final AdapterFamily<Entity>      ENTITY      = new AdapterFamily<>();
    public static final AdapterFamily<Block>       BLOCK       = new AdapterFamily<>();
    public static final AdapterFamily<BlockState>  BLOCK_STATE = new AdapterFamily<>();
    public static final AdapterFamily<ItemStack>   ITEM        = new AdapterFamily<>();
    public static final AdapterFamily<Enchantment> ENCHANTMENT = new AdapterFamily<>();

    private final Set<Adapter<?, O>> adapterByKey;

    public AdapterFamily() {
        this.adapterByKey = new HashSet<>();
    }

    public <I, E extends Adapter<I, O>> void addAdapter(@NotNull E adapter) {
        this.adapterByKey.add(adapter);
    }

    @NotNull
    public Set<Adapter<?, O>> getAdapters() {
        return this.adapterByKey;
    }

    @NotNull
    public Set<Adapter<?, O>> getAdaptersFor(@NotNull O entity) {
        return this.adapterByKey.stream().filter(adapter -> adapter.canHandle(entity)).collect(Collectors.toSet());
    }

    @Nullable
    public Adapter<?, O> getAdapterFor(@NotNull O entity) {
        return this.getAdaptersFor(entity).stream().findFirst().orElse(null);
    }

    @Nullable
    public Adapter<?, O> getAdapterForName(@NotNull String fullName) {
        return this.getAdapters().stream().filter(adapter -> adapter.canHandle(fullName)).findFirst().orElse(null);
    }
}
