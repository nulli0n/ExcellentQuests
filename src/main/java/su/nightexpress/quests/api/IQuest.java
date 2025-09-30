package su.nightexpress.quests.api;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.manager.ConfigBacked;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.api.exception.QuestLoadException;

import java.util.List;

public interface IQuest extends ConfigBacked/*, Writeable*/ {

    void load() throws QuestLoadException;

    @NotNull TaskType<?, ?> getType();

    @NotNull String getId();

    @NotNull String getName();

    @NotNull List<String> getDescription();

    @NotNull NightItem getIcon();
}
