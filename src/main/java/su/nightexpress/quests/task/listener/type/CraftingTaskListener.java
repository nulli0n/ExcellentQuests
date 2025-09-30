package su.nightexpress.quests.task.listener.type;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.listener.TaskListener;

public class CraftingTaskListener extends TaskListener<ItemStack, AdapterFamily<ItemStack>> {

    public CraftingTaskListener(@NotNull QuestsPlugin plugin, @NotNull TaskManager manager, @NotNull TaskType<ItemStack, AdapterFamily<ItemStack>> taskType) {
        super(plugin, manager, taskType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTaskCrafting(CraftItemEvent event) {
        if (event.getClick() == ClickType.MIDDLE) return;

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType().isAir()) return;

        Player player = (Player) event.getWhoClicked();
        if (!this.manager.canDoTasks(player)) return;

        ItemStack craftedItem = new ItemStack(itemStack);
        int unitSize = craftedItem.getAmount();

        if (event.isShiftClick()) {
            int has = Players.countItem(player, craftedItem);
            this.plugin.runTask(() -> {
                int now = Players.countItem(player, craftedItem);
                int crafted = now - has;
                //int craftedUnits = crafted / unitSize;

                this.progressQuests(player, craftedItem, crafted);
            });
            return;
        }

        ItemStack cursor = event.getCursor();
        if (cursor.getType().isAir()) {
            this.progressQuests(player, craftedItem, unitSize);
            return;
        }

        if (!craftedItem.isSimilar(cursor) || cursor.getAmount() + unitSize > cursor.getMaxStackSize()) {
            return;
        }

        this.progressQuests(player, craftedItem, unitSize);
    }
}
