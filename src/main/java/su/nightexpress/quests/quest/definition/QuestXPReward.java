package su.nightexpress.quests.quest.definition;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

public class QuestXPReward implements Writeable {

    private final double base;
    private final double unitBonus;

    public QuestXPReward(double base, double unitBonus) {
        this.base = base;
        this.unitBonus = unitBonus;
    }

    @NotNull
    public static QuestXPReward read(@NotNull FileConfig config, @NotNull String path) {
        double base = ConfigValue.create(path + ".Base", 0D).read(config);
        double unitBonus = ConfigValue.create(path + ".UnitBonus", 0D).read(config);

        return new QuestXPReward(base, unitBonus);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Base", this.base);
        config.set(path + ".UnitBonus", this.unitBonus);
    }

    public int getXP(double unitsWorth) {
        return (int) Math.ceil(this.base + (unitsWorth * this.unitBonus));
    }

    public double getBase() {
        return this.base;
    }

    public double getUnitBonus() {
        return this.unitBonus;
    }
}
