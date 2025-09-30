package su.nightexpress.quests.task.listener.type;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.listener.TaskListener;

public class GrindstoneTaskListener extends TaskListener<ItemStack, AdapterFamily<ItemStack>> {

    public GrindstoneTaskListener(@NotNull QuestsPlugin plugin, @NotNull TaskManager manager, @NotNull TaskType<ItemStack, AdapterFamily<ItemStack>> taskType) {
        super(plugin, manager, taskType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTaskGrindstone(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof GrindstoneInventory inventory)) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!this.manager.canDoTasks(player)) return;

        if (event.getRawSlot() != 2 || event.getClick() == ClickType.MIDDLE) return;

        ItemStack result = inventory.getItem(2);
        if (result == null || result.getType().isAir()) return;

        ItemStack source = inventory.getItem(0);
        if (source == null || result.getType().isAir()) return;

        if (source.getEnchantments().size() == result.getEnchantments().size()) return;

        this.progressQuests(player, result);
    }
}
