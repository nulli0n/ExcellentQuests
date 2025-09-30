package su.nightexpress.quests.task.listener.type;

import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.listener.TaskListener;
import su.nightexpress.quests.task.workstation.Workstation;
import su.nightexpress.quests.task.workstation.WorkstationMode;

public class CookingTaskListener extends TaskListener<ItemStack, AdapterFamily<ItemStack>> {

    public CookingTaskListener(@NotNull QuestsPlugin plugin, @NotNull TaskManager manager, @NotNull TaskType<ItemStack, AdapterFamily<ItemStack>> taskType) {
        super(plugin, manager, taskType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTaskCooking(BlockCookEvent event) {
        Block block = event.getBlock();
        Workstation workstation = Workstation.getByBlock(block);
        if (workstation == null) return;

        TileState tile = workstation.getBackend();

        Player player = this.manager.getWorkstationOwner(tile);
        if (player == null) return;

        if (!this.manager.canDoTasks(player)) return;

        ItemStack ingredient = event.getSource();
        WorkstationMode mode = this.manager.getWorkstationMode(tile);
        if (mode == WorkstationMode.AUTO && !Config.ANTI_ABUSE_COUNT_AUTO_COOKING.get()) return;

        this.progressQuests(player, ingredient);
    }
}
