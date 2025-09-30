package su.nightexpress.quests.quest.data;

import org.jetbrains.annotations.NotNull;

public class QuestCounter {

    private final int    required;
    private final double unitWorth;

    private int completed;

    public QuestCounter(int required, int completed, double unitWorth) {
        this.required = required;
        this.completed = completed;
        this.unitWorth = unitWorth;
    }

    @NotNull
    public static QuestCounter create(int required, double unitWorth) {
        return new QuestCounter(required, 0, unitWorth);
    }

    public boolean isCompleted() {
        return this.completed >= this.required;
    }

    public void addCompleted(int amount) {
        this.setCompleted(this.completed + amount);
    }

    public void setCompleted(int completed) {
        this.completed = Math.min(completed, this.required);
    }

    public int getRequired() {
        return this.required;
    }

    public int getCompleted() {
        return this.completed;
    }

    public double getUnitWorth() {
        return this.unitWorth;
    }
}
