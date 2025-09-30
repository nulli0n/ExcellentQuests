package su.nightexpress.quests.task.listener.type;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.listener.TaskListener;

public class MobDropTaskListener extends TaskListener<ItemStack, AdapterFamily<ItemStack>> {

    public MobDropTaskListener(@NotNull QuestsPlugin plugin, @NotNull TaskManager manager, @NotNull TaskType<ItemStack, AdapterFamily<ItemStack>> taskType) {
        super(plugin, manager, taskType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTaskEntityDrop(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) return;

        Player player = entity.getKiller();
        if (player == null) return;
        if (!this.manager.canDoTasks(player)) return;
        if (this.manager.isSpawnerMob(entity)) return;

        event.getDrops().forEach(itemStack -> {
            this.progressQuests(player, itemStack, itemStack.getAmount());
        });
    }
}
