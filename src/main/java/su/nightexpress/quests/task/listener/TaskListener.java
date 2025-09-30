package su.nightexpress.quests.task.listener;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.task.TaskType;

public abstract class TaskListener<O, A extends AdapterFamily<O>> extends AbstractListener<QuestsPlugin> {

    protected final TaskManager    manager;
    protected final TaskType<O, A> taskType;

    public TaskListener(@NotNull QuestsPlugin plugin, @NotNull TaskManager manager, @NotNull TaskType<O, A> taskType) {
        super(plugin);
        this.manager = manager;
        this.taskType = taskType;
    }

    protected void progressQuests(@NotNull Player player, @NotNull O entity) {
        this.progressQuests(player, entity, 1);
    }

    protected void progressQuests(@NotNull Player player, @NotNull O entity, int amount) {
        this.manager.progressQuests(player, this.taskType, entity, amount);
    }
}
