package su.nightexpress.quests.task.listener.type;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.listener.TaskListener;

public class KillingTaskListener extends TaskListener<Entity, AdapterFamily<Entity>> {

    public KillingTaskListener(@NotNull QuestsPlugin plugin, @NotNull TaskManager manager, @NotNull TaskType<Entity, AdapterFamily<Entity>> taskType) {
        super(plugin, manager, taskType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTaskKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (this.manager.isSpawnerMob(entity)) return;

        Player player = entity.getKiller();
        if (player == null || !this.manager.canDoTasks(player)) return;

        this.progressQuests(player, entity);
    }
}
