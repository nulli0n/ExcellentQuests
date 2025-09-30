package su.nightexpress.quests;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.battlepass.BattlePassManager;
import su.nightexpress.quests.milestone.MilestoneManager;
import su.nightexpress.quests.quest.QuestManager;
import su.nightexpress.quests.reward.RewardManager;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.user.UserManager;

import java.util.Optional;

public class QuestsAPI {

    private static QuestsPlugin plugin;

    static void load(@NotNull QuestsPlugin instace) {
        plugin = instace;
    }

    static void shutdown() {
        plugin = null;
    }

    public static boolean isLoaded() {
        return plugin != null;
    }

    @NotNull
    public static QuestsPlugin plugin() {
        if (plugin == null) throw new IllegalStateException("API is not yet initialized!");

        return plugin;
    }

    @NotNull
    public static UserManager getUserManager() {
        return plugin().getUserManager();
    }

    @NotNull
    public static RewardManager getRewardManager() {
        return plugin().getRewardManager();
    }

    @NotNull
    public static TaskManager getTaskManager() {
        return plugin().getTaskManager();
    }

    @NotNull
    public static Optional<BattlePassManager> battlePassManager() {
        return plugin().battlePassManager();
    }

    @NotNull
    public static Optional<MilestoneManager> milestoneManager() {
        return plugin().milestoneManager();
    }

    @NotNull
    public static Optional<QuestManager> questManager() {
        return plugin().questManager();
    }
}
