package su.nightexpress.quests.quest.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.TimeUtil;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class QuestData {

    private final UUID                      id;
    private final String                    questId;
    private final Map<String, QuestCounter> objectiveCounter;
    private final Set<String>               rewardIds;
    private final double                    scale;
    private final int xpReward;

    private boolean active;
    private long    expireDate;

    public QuestData(@NotNull UUID id,
                     @NotNull String questId,
                     @NotNull Map<String, QuestCounter> objectiveCounter,
                     @NotNull Set<String> rewardIds,
                     double scale,
                     int xpReward,
                     boolean active,
                     long expireDate) {
        this.id = id;
        this.questId = questId;
        this.objectiveCounter = objectiveCounter;
        this.rewardIds = rewardIds;
        this.scale = scale;
        this.xpReward = xpReward;
        this.active = active;
        this.expireDate = expireDate;
    }

    public double getProgressValue() {
        return (double) this.countTotalProgress() / (double) this.countTotalRequirement();
    }

    public int countTotalRequirement() {
        return this.objectiveCounter.values().stream().mapToInt(QuestCounter::getRequired).sum();
    }

    public int countTotalProgress() {
        return this.objectiveCounter.values().stream().mapToInt(QuestCounter::getCompleted).sum();
    }

    public int countUnitsWorth() {
        return this.objectiveCounter.values().stream().mapToInt(counter -> (int) Math.ceil(counter.getRequired() * counter.getUnitWorth())).sum();
    }

    /*public int countCompletedUnitsWithWeight() {
        return this.objectiveCounter.values().stream().mapToInt(counter -> (int) Math.ceil(counter.getCompleted() * counter.getUnitWeight())).sum();
    }*/

    public boolean isCompleted() {
        return this.objectiveCounter.values().stream().allMatch(QuestCounter::isCompleted);
    }

    public boolean isExpired() {
        return TimeUtil.isPassed(this.expireDate);
    }

    public int getRequired(@NotNull String fullName) {
        return this.objectiveCounter(fullName).map(QuestCounter::getRequired).orElse(0);
    }

    public int getCurrent(@NotNull String fullName) {
        return this.objectiveCounter(fullName).map(QuestCounter::getCompleted).orElse(0);
    }

    public void addCompleted(@NotNull String fullName, int amount) {
        this.objectiveCounter(fullName).ifPresent(counter -> counter.addCompleted(amount));
    }

    public boolean hasObjective(@NotNull String fullName) {
        return this.getObjectiveCounter(fullName) != null;
    }

    @NotNull
    public Optional<QuestCounter> objectiveCounter(@NotNull String fullName) {
        return Optional.ofNullable(this.getObjectiveCounter(fullName));
    }

    @Nullable
    public QuestCounter getObjectiveCounter(@NotNull String fullName) {
        return this.objectiveCounter.get(LowerCase.INTERNAL.apply(fullName));
    }

    @NotNull
    public UUID getId() {
        return this.id;
    }

    @NotNull
    public String getQuestId() {
        return this.questId;
    }

    public double getScale() {
        return this.scale;
    }

    public int getXPReward() {
        return this.xpReward;
    }

    @NotNull
    public Map<String, QuestCounter> getObjectiveCounterMap() {
        return this.objectiveCounter;
    }

    @NotNull
    public Set<String> getRewardIds() {
        return this.rewardIds;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getExpireDate() {
        return this.expireDate;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }
}
