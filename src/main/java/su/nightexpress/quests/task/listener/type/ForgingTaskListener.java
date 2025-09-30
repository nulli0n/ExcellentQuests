package su.nightexpress.quests.task.listener.type;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.listener.TaskListener;

public class ForgingTaskListener extends TaskListener<ItemStack, AdapterFamily<ItemStack>> {

    public ForgingTaskListener(@NotNull QuestsPlugin plugin, @NotNull TaskManager manager, @NotNull TaskType<ItemStack, AdapterFamily<ItemStack>> taskType) {
        super(plugin, manager, taskType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTaskForging(InventoryClickEvent event) {
        if (!(event.getView() instanceof AnvilView anvil)) return;
        if (event.getRawSlot() != 2 || event.getClick() == ClickType.MIDDLE) return;

        if (anvil.getRepairCost() <= 0) return;

        ItemStack first = anvil.getItem(0);
        if (first == null || first.getType().isAir()) return;

        ItemStack result = anvil.getItem(2);
        if (result == null || result.getType().isAir()) return;

        if (first.getType() != result.getType() || result.isSimilar(first)) return;

        Player player = (Player) event.getWhoClicked();
        if (!this.manager.canDoTasks(player)) return;

        ItemStack resultCopy = new ItemStack(result);

        this.plugin.runTask(task -> {
            ItemStack updated = anvil.getItem(2);
            if (updated != null && !updated.getType().isAir()) return;

            this.progressQuests(player, resultCopy);
        });
    }
}
