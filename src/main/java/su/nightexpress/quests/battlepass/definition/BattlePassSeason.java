package su.nightexpress.quests.battlepass.definition;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.quests.QuestsPlaceholders;

import java.util.UUID;
import java.util.function.UnaryOperator;

public class BattlePassSeason {

    private final UUID   id;
    private final String name;
    private final long   startDate;
    private final long   endDate;
    private final long   expireDate;

    private boolean launched;

    public BattlePassSeason(@NotNull UUID id, @NotNull String name, long startDate, long endDate, long expireDate, boolean launched) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expireDate = expireDate;
        this.launched = launched;
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return QuestsPlaceholders.SEASON.replacer(this);
    }

    @NotNull
    public UUID getId() {
        return this.id;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public boolean isLaunchTime() {
        return !this.isLaunched() && TimeUtil.isPassed(this.startDate) && !this.isPast();
    }

    public boolean isRunning() {
        return this.isLaunched() && !this.isPast();
    }

    public boolean isPast() {
        return TimeUtil.isPassed(this.endDate);
    }

    public boolean isScheduled() {
        return !this.isLaunched() && System.currentTimeMillis() < this.endDate && this.endDate > this.startDate;
    }

    public boolean isExpired() {
        return TimeUtil.isPassed(this.expireDate);
    }

    public boolean isLaunched() {
        return this.launched;
    }

    public void setLaunched(boolean launched) {
        this.launched = launched;
    }

    public long getDuration() {
        return this.endDate - this.startDate;
    }

    public long getStartDate() {
        return this.startDate;
    }

    public long getEndDate() {
        return this.endDate;
    }

    public long getExpireDate() {
        return this.expireDate;
    }
}
