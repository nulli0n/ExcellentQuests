package su.nightexpress.quests.task.listener.type;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.listener.TaskListener;

public class MilkingTaskListener extends TaskListener<Entity, AdapterFamily<Entity>> {

    public MilkingTaskListener(@NotNull QuestsPlugin plugin, @NotNull TaskManager manager, @NotNull TaskType<Entity, AdapterFamily<Entity>> taskType) {
        super(plugin, manager, taskType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTaskMilk(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        if (this.manager.canDoTasks(player)) return;

        if (event.getItemStack() == null) return;
        if (event.getItemStack().getType() != Material.MILK_BUCKET) return;

        Location eyes = player.getEyeLocation();
        Location location = player.getLocation();
        RayTraceResult result = player.getWorld().rayTraceEntities(eyes, location.getDirection(), 5D, entity -> !(entity instanceof Player));
        if (result == null) return;

        Entity entity = result.getHitEntity();
        if (entity == null || this.manager.isSpawnerMob(entity)) return;

        this.progressQuests(player, entity);
    }
}
