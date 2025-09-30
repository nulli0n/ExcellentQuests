package su.nightexpress.quests.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.db.AbstractUser;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.quests.battlepass.definition.BattlePassSeason;
import su.nightexpress.quests.milestone.data.MilestoneData;
import su.nightexpress.quests.quest.data.QuestData;
import su.nightexpress.quests.milestone.definition.Milestone;
import su.nightexpress.quests.battlepass.data.BattlePassData;

import java.util.*;

public class QuestUser extends AbstractUser {

    private final Map<UUID, BattlePassData>  battlePassData;
    private final Map<UUID, QuestData>       questData;
    private final Map<String, MilestoneData> milestoneData;

    private long newQuestsDate;

    public QuestUser(@NotNull UUID uuid,
                     @NotNull String name,
                     long dateCreated,
                     long lastOnline,
                     long newQuestsDate,
                     @NotNull Map<UUID, BattlePassData> battlePassData,
                     @NotNull Map<UUID, QuestData> questData,
                     @NotNull Map<String, MilestoneData> milestoneData) {
        super(uuid, name, dateCreated, lastOnline);
        this.setNewQuestsDate(newQuestsDate);
        this.battlePassData = battlePassData;
        this.questData = questData;
        this.milestoneData = milestoneData;
    }

    public int countQuestsAmount() {
        return this.questData.size();
    }

    public boolean isNewQuestsTime() {
        return TimeUtil.isPassed(this.newQuestsDate);
    }

    public long getNewQuestsDate() {
        return this.newQuestsDate;
    }

    public void setNewQuestsDate(long newQuestsDate) {
        this.newQuestsDate = newQuestsDate;
    }

    public boolean hasMilestone(@NotNull Milestone milestone) {
        return this.hasMilestone(milestone.getId());
    }

    public boolean hasMilestone(@NotNull String id) {
        return this.milestoneData.containsKey(id);
    }

    public boolean addMilestone(@NotNull Milestone milestone) {
        if (this.hasMilestone(milestone)) return false;

        this.milestoneData.put(milestone.getId(), MilestoneData.create(milestone));
        return true;
    }

    public int getMilestoneCompletedLevels(@NotNull Milestone milestone) {
        MilestoneData data = this.getMilestoneData(milestone);
        return data.countCompletedLevels();
    }

    public boolean isCompleted(@NotNull Milestone milestone) {
        return this.getMilestoneCompletedLevels(milestone) >= milestone.getLevels();
    }

    public void clearQuestData() {
        this.questData.clear();
    }

    public void clearMilestoneData() {
        this.milestoneData.clear();
    }

    @NotNull
    public Map<UUID, BattlePassData> getBattlePassData() {
        return this.battlePassData;
    }

    @NotNull
    public BattlePassData getBattlePassData(@NotNull BattlePassSeason season) {
        return this.battlePassData.computeIfAbsent(season.getId(), k -> BattlePassData.create(season.getExpireDate()));
    }

    @Nullable
    public BattlePassData getBattlePassData(@NotNull UUID id) {
        return this.battlePassData.get(id);
    }

    public void addQuestData(@NotNull QuestData questData) {
        this.questData.put(questData.getId(), questData);
    }

    @NotNull
    public Set<QuestData> getQuestDatas() {
        return new HashSet<>(this.questData.values());
    }

    @NotNull
    public Map<UUID, QuestData> getQuestData() {
        return this.questData;
    }

    @NotNull
    public Map<String, MilestoneData> getMilestoneDataMap() {
        return this.milestoneData;
    }

    @NotNull
    public Set<MilestoneData> getMilestoneDatas() {
        return new HashSet<>(this.milestoneData.values());
    }

    @NotNull
    public MilestoneData getMilestoneData(@NotNull Milestone milestone) {
        if (!this.hasMilestone(milestone)) {
            this.addMilestone(milestone);
        }
        return this.milestoneData.get(milestone.getId());
    }

    @Nullable
    public MilestoneData getMilestoneData(@NotNull String id) {
        return this.milestoneData.get(id);
    }
}
