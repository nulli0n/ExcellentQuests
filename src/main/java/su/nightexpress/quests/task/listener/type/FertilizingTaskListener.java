package su.nightexpress.quests.task.listener.type;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.listener.TaskListener;

import java.util.HashSet;
import java.util.Set;

public class FertilizingTaskListener extends TaskListener<BlockState, AdapterFamily<BlockState>> {

    public FertilizingTaskListener(@NotNull QuestsPlugin plugin, @NotNull TaskManager manager, @NotNull TaskType<BlockState, AdapterFamily<BlockState>> taskType) {
        super(plugin, manager, taskType);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTaskFertilize(BlockFertilizeEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        if (!this.manager.canDoTasks(player)) return;

        Set<BlockState> blocks = new HashSet<>();
        blocks.add(event.getBlock().getState());
        blocks.addAll(event.getBlocks());

        blocks.forEach(blockState -> {
            this.progressQuests(player, blockState);
        });
    }
}
