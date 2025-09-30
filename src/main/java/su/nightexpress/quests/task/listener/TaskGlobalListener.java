package su.nightexpress.quests.task.listener;

import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.util.blocktracker.PlayerBlockTracker;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.task.workstation.Workstation;
import su.nightexpress.quests.task.workstation.WorkstationMode;

import java.util.function.Consumer;

public class TaskGlobalListener extends AbstractListener<QuestsPlugin> {

    private final TaskManager manager;

    public TaskGlobalListener(@NotNull QuestsPlugin plugin, @NotNull TaskManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorkstationPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Workstation workstation = Workstation.getByBlock(block);
        if (workstation == null) return;

        Player player = event.getPlayer();

        this.modifyStandNextTick(workstation, station -> this.manager.setWorkstationOwnerId(station, player.getUniqueId()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorkstationAutomaticInventoryMoveEvent(InventoryMoveItemEvent event) {
        Inventory from = event.getSource();
        if (from.getType() != InventoryType.HOPPER) return;

        Inventory to = event.getDestination();
        Workstation workstation = Workstation.getByInventory(to);
        if (workstation == null) return;

        this.modifyStandNextTick(workstation, station -> this.manager.setWorkstationMode(station, WorkstationMode.AUTO));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorkstationMenuClick(InventoryClickEvent event) {
        this.handleWorkstationInteraction(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorkstationMenuDrag(InventoryDragEvent event) {
        this.handleWorkstationInteraction(event);
    }

    private void handleWorkstationInteraction(@NotNull InventoryInteractEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Workstation workstation = Workstation.getByInventory(event.getInventory());
        if (workstation == null || workstation.isCrafting()) return;

        this.modifyStandNextTick(workstation, station -> {
            this.manager.setWorkstationOwnerId(station, player.getUniqueId());
            this.manager.setWorkstationMode(station, WorkstationMode.MANUAL);
        });
    }

    private void modifyStandNextTick(@NotNull Workstation workstation, @NotNull Consumer<TileState> consumer) {
        this.plugin.runTask(() -> {
            if (!(workstation.getBackend().getLocation().getBlock().getState() instanceof TileState tickedStation)) return;

            consumer.accept(tickedStation);
            tickedStation.update(true, false);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAntiAbuseArtificalMobSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

        if (this.manager.isArtificalSpawn(reason)) {
            this.manager.markSpawnerMob(entity, true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAntiAbuseArtificalMobTransform(EntityTransformEvent event) {
        if (this.manager.isSpawnerMob(event.getEntity())) {
            event.getTransformedEntities().forEach(entity -> this.manager.markSpawnerMob(entity, true));
            this.manager.markSpawnerMob(event.getTransformedEntity(), true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        // An extra marker that a block was placed by a player for the #isPlayerBlock method,
        // that will handle both, BlockBreak and BlockDrop phases.
        if (PlayerBlockTracker.isTracked(block) && event.isDropItems()) {
            this.manager.markPlayerBlock(block, true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockDrop(BlockDropItemEvent event) {
        Block block = event.getBlock();
        this.manager.markPlayerBlock(block, false);
    }
}
