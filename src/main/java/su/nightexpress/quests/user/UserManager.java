package su.nightexpress.quests.user;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.db.AbstractUserManager;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.battlepass.data.BattlePassData;
import su.nightexpress.quests.data.DataHandler;
import su.nightexpress.quests.milestone.data.MilestoneData;
import su.nightexpress.quests.quest.data.QuestData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager extends AbstractUserManager<QuestsPlugin, QuestUser> {

    public UserManager(@NotNull QuestsPlugin plugin, @NotNull DataHandler dataHandler) {
        super(plugin, dataHandler);
    }

    @Override
    @NotNull
    public QuestUser create(@NotNull UUID uuid, @NotNull String name) {
        long dateCreated = System.currentTimeMillis();
        long newQuestsDate = 0L;
        Map<UUID, BattlePassData> battlePassData = new HashMap<>();
        Map<UUID, QuestData> questData = new HashMap<>();
        Map<String, MilestoneData> milestoneData = new HashMap<>();

        return new QuestUser(uuid, name, dateCreated, dateCreated, newQuestsDate, battlePassData, questData, milestoneData);
    }
}
