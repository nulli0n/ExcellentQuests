package su.nightexpress.quests.task.listener.type;

import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.listener.TaskListener;
import su.nightexpress.quests.task.workstation.WorkstationMode;

public class BrewingTaskListener extends TaskListener<ItemStack, AdapterFamily<ItemStack>> {

    public BrewingTaskListener(@NotNull QuestsPlugin plugin, @NotNull TaskManager manager, @NotNull TaskType<ItemStack, AdapterFamily<ItemStack>> taskType) {
        super(plugin, manager, taskType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTaskBrewEnd(BrewEvent event) {
        BrewerInventory brewerInventory = event.getContents();
        BrewingStand stand = brewerInventory.getHolder();
        if (stand == null) return;

        ItemStack ingredient = brewerInventory.getIngredient();
        if (ingredient == null || ingredient.getType().isAir()) return;

        Player player = this.manager.getWorkstationOwner(stand);
        if (player == null) return;

        if (!this.manager.canDoTasks(player)) return;

        int potionsAmount = event.getResults().size();
        WorkstationMode mode = this.manager.getWorkstationMode(stand);
        if (mode == WorkstationMode.AUTO && !Config.ANTI_ABUSE_COUNT_AUTO_COOKING.get()) return;

        this.progressQuests(player, ingredient, potionsAmount);
    }
}
