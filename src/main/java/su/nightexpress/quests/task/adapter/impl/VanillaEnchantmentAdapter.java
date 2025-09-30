package su.nightexpress.quests.task.adapter.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.LangUtil;
import su.nightexpress.nightcore.util.bridge.RegistryType;
import su.nightexpress.quests.task.adapter.type.AbstractAdapter;

public class VanillaEnchantmentAdapter extends AbstractAdapter<NamespacedKey, Enchantment> {

    public VanillaEnchantmentAdapter(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean canHandle(@NotNull Enchantment enchantment) {
        return true;
    }

    @Override
    public boolean canHandle(@NotNull String fullName) {
        return BukkitThing.getEnchantment(fullName) != null;
    }

    @Override
    @Nullable
    public NamespacedKey getTypeByName(@NotNull String name) {
        return BukkitThing.parseKey(name);
    }

    @Override
    @Nullable
    public NamespacedKey getType(@NotNull Enchantment enchantment) {
        return enchantment.getKey();
    }

    @Override
    @NotNull
    public String getTypeName(@NotNull NamespacedKey key) {
        return BukkitThing.getAsString(key);
    }

    @Override
    @NotNull
    public String toFullNameOfType(@NotNull NamespacedKey key) {
        return BukkitThing.getAsString(key);
    }

    @Override
    @Nullable
    public String getLocalizedName(@NotNull String fullName) {
        return this.getLocalizedName(BukkitThing.parseKey(fullName));
    }

    @Override
    @Nullable
    public String getLocalizedName(@NotNull NamespacedKey key) {
        Enchantment enchantment = BukkitThing.getByKey(RegistryType.ENCHANTMENT, key);
        return enchantment == null ? null : LangUtil.getSerializedName(enchantment);
    }
}
