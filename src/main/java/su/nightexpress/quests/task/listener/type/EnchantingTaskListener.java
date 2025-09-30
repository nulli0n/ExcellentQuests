package su.nightexpress.quests.task.listener.type;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.listener.TaskListener;

public class EnchantingTaskListener extends TaskListener<Enchantment, AdapterFamily<Enchantment>> {

    public EnchantingTaskListener(@NotNull QuestsPlugin plugin, @NotNull TaskManager manager, @NotNull TaskType<Enchantment, AdapterFamily<Enchantment>> taskType) {
        super(plugin, manager, taskType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTaskEnchanting(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        if (!this.manager.canDoTasks(player)) return;

        event.getEnchantsToAdd().forEach((enchantment, level) -> {
            this.progressQuests(player, enchantment);
        });
    }
}
