package su.nightexpress.quests.task;

import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.Enums;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.PDCUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.blocktracker.PlayerBlockTracker;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.task.adapter.Adapter;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.registry.Registries;
import su.nightexpress.quests.task.listener.TaskGlobalListener;
import su.nightexpress.quests.task.listener.type.*;
import su.nightexpress.quests.task.workstation.WorkstationMode;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import static org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class TaskManager extends AbstractManager<QuestsPlugin> {

    private static final Predicate<Block> BLOCK_FILTER = block -> true;
    private static final String PLAYER_BLOCK_MARKER = "player_block_marker";

    private final Set<SpawnReason> artificalMobSpawns;

    private final NamespacedKey stationOwnerKey;
    private final NamespacedKey stationModeKey;
    private final NamespacedKey mobSpawnerKey;

    public TaskManager(@NotNull QuestsPlugin plugin) {
        super(plugin);
        this.artificalMobSpawns = new HashSet<>();
        this.stationOwnerKey = new NamespacedKey(plugin, "workstation.owner_id");
        this.stationModeKey = new NamespacedKey(plugin, "workstation.craft_mode");
        this.mobSpawnerKey = new NamespacedKey(plugin, "spawner_mob");
    }

    @Override
    protected void onLoad() {
        if (!Config.ANTI_ABUSE_COUNT_PLAYER_BLOCKS.get()) {
            PlayerBlockTracker.initialize();
            PlayerBlockTracker.BLOCK_FILTERS.add(BLOCK_FILTER);
        }

        if (!Config.ANTI_ABUSE_COUNT_ARTIFICAL_MOBS.get()) {
            this.artificalMobSpawns.addAll(Lists.modify(Config.ANTI_ABUSE_ARTIFICAL_MOB_SPAWNS.get(), str -> Enums.get(str, SpawnReason.class)));
        }

        this.registerTaskTypes();

        this.addListener(new TaskGlobalListener(this.plugin, this));
    }

    @Override
    protected void onShutdown() {

    }

    private void registerTaskTypes() {
        Registries.registerTaskType(TaskTypeId.PLACE_BLOCK, AdapterFamily.BLOCK, taskType -> this.addListener(new BlockPlaceTaskListener(this.plugin, this, taskType)));
        Registries.registerTaskType(TaskTypeId.BREAK_BLOCK, AdapterFamily.BLOCK, type -> this.addListener(new BlockBreakTaskListener(this.plugin, this, type)));
        Registries.registerTaskType(TaskTypeId.BREED_MOB, AdapterFamily.ENTITY, taskType -> this.addListener(new BreedingTaskListener(this.plugin, this, taskType)));
        Registries.registerTaskType(TaskTypeId.BREWING, AdapterFamily.ITEM, taskType -> this.addListener(new BrewingTaskListener(this.plugin, this, taskType)));
        Registries.registerTaskType(TaskTypeId.COOK_ITEM, AdapterFamily.ITEM, taskType -> this.addListener(new CookingTaskListener(this.plugin, this, taskType)));
        Registries.registerTaskType(TaskTypeId.CRAFT_ITEM, AdapterFamily.ITEM, taskType -> this.addListener(new CraftingTaskListener(this.plugin, this, taskType)));
        Registries.registerTaskType(TaskTypeId.ENCHANTING, AdapterFamily.ENCHANTMENT, taskType -> this.addListener(new EnchantingTaskListener(this.plugin, this, taskType)));
        Registries.registerTaskType(TaskTypeId.FERTILIZING, AdapterFamily.BLOCK_STATE, type -> this.addListener(new FertilizingTaskListener(this.plugin, this, type)));
        Registries.registerTaskType(TaskTypeId.FISH_ITEM, AdapterFamily.ITEM, type -> this.addListener(new FishingTaskListener(this.plugin, this, type)));
        Registries.registerTaskType(TaskTypeId.FORGE_ITEM, AdapterFamily.ITEM, type -> this.addListener(new ForgingTaskListener(this.plugin, this, type)));
        Registries.registerTaskType(TaskTypeId.BLOCK_LOOT, AdapterFamily.ITEM, type -> this.addListener(new BlockDropTaskListener(this.plugin, this, type)));
        Registries.registerTaskType(TaskTypeId.MOB_LOOT, AdapterFamily.ITEM, type -> this.addListener(new MobDropTaskListener(this.plugin, this, type)));
        Registries.registerTaskType(TaskTypeId.GRINDSTONE_ITEM, AdapterFamily.ITEM, type -> this.addListener(new GrindstoneTaskListener(this.plugin, this, type)));
        Registries.registerTaskType(TaskTypeId.KILL_MOB, AdapterFamily.ENTITY, type -> this.addListener(new KillingTaskListener(this.plugin, this, type)));
        Registries.registerTaskType(TaskTypeId.MILK_MOB, AdapterFamily.ENTITY, type -> this.addListener(new MilkingTaskListener(this.plugin, this, type)));
        Registries.registerTaskType(TaskTypeId.SHEAR_MOB, AdapterFamily.ENTITY, type -> this.addListener(new ShearingTaskListener(this.plugin, this, type)));
        Registries.registerTaskType(TaskTypeId.TAME_MOB, AdapterFamily.ENTITY, type -> this.addListener(new TamingTaskListener(this.plugin, this, type)));
    }

    public boolean canDoTasks(@NotNull Player player) {
        return player.getGameMode() != GameMode.CREATIVE && canDoTasksInVehicle(player);
    }

    public static boolean canDoTasksInVehicle(@NotNull Player player) {
        if (Config.ANTI_ABUSE_COUNT_IN_VEHICLES.get()) return true;

        Entity vehicle = player.getVehicle();
        return vehicle == null || vehicle instanceof LivingEntity;
    }

    public <O, A extends AdapterFamily<O>> void progressQuests(@NotNull Player player, @NotNull TaskType<O, A> taskType, @NotNull O entity, int amount) {
        Adapter<?, O> adapter = taskType.getAdapterFamily().getAdapterFor(entity);
        if (adapter == null) return;

        String fullName = adapter.toFullNameOfEntity(entity);
        if (fullName == null) return;

        this.plugin.milestoneManager().ifPresent(milestoneManager -> milestoneManager.progressMilestones(player, taskType, fullName, amount));
        this.plugin.questManager().ifPresent(questManager -> questManager.progressQuests(player, taskType, fullName, amount));
    }

    public boolean isArtificalSpawn(@NotNull SpawnReason reason) {
        return this.artificalMobSpawns.contains(reason);
    }

    public boolean isSpawnerMob(@NotNull Entity entity) {
        return PDCUtil.getBoolean(entity, this.mobSpawnerKey).isPresent();
    }

    public void markSpawnerMob(@NotNull Entity entity, boolean flag) {
        PDCUtil.set(entity, this.mobSpawnerKey, flag);
    }

    public boolean isPlayerBlock(@NotNull Block block) {
        return block.hasMetadata(PLAYER_BLOCK_MARKER) || PlayerBlockTracker.isTracked(block);
    }

    public void markPlayerBlock(@NotNull Block block, boolean flag) {
        if (flag) {
            block.setMetadata(PLAYER_BLOCK_MARKER, new FixedMetadataValue(this.plugin, true));
        }
        else {
            block.removeMetadata(PLAYER_BLOCK_MARKER, this.plugin);
        }
    }

    public void setWorkstationOwnerId(@NotNull TileState station, @NotNull UUID uuid) {
        PDCUtil.set(station, this.stationOwnerKey, uuid);
    }

    @Nullable
    public UUID getWorkstationOwnerId(@NotNull TileState station) {
        return PDCUtil.getUUID(station, this.stationOwnerKey).orElse(null);
    }

    @Nullable
    public Player getWorkstationOwner(@NotNull TileState station) {
        UUID uuid = getWorkstationOwnerId(station);
        return uuid == null ? null : Players.getPlayer(uuid);
    }

    public void setWorkstationMode(@NotNull TileState station, @NotNull WorkstationMode mode) {
        PDCUtil.set(station, this.stationModeKey, mode.getId());
    }

    @Nullable
    public WorkstationMode getWorkstationMode(@NotNull TileState station) {
        return PDCUtil.getInt(station, this.stationModeKey).map(WorkstationMode::byId).orElse(null);
    }
}
