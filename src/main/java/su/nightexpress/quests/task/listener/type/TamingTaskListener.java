package su.nightexpress.quests.task.listener.type;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTameEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.listener.TaskListener;

public class TamingTaskListener extends TaskListener<Entity, AdapterFamily<Entity>> {

    public TamingTaskListener(@NotNull QuestsPlugin plugin, @NotNull TaskManager manager, @NotNull TaskType<Entity, AdapterFamily<Entity>> taskType) {
        super(plugin, manager, taskType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTaskTame(EntityTameEvent event) {
        Player player = (Player) event.getOwner();
        if (!this.manager.canDoTasks(player)) return;

        LivingEntity entity = event.getEntity();
        if (this.manager.isSpawnerMob(entity)) return;

        this.progressQuests(player, entity);
    }
}
