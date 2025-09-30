package su.nightexpress.quests.quest.definition;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.quests.util.SectionedData;

public record QuestObjective(int min, int max, double weight, double unitWorth) implements Writeable {

    @NotNull
    public static QuestObjective deserialize(@NotNull String rawData) {
        SectionedData data = SectionedData.deserialize(rawData);

        int min = data.getInt(0, 0, 0);
        int max = data.getInt(0, 1, 0);
        double weight = data.getDouble(1, 0, 0);
        double unitWorth = data.getDouble(2, 0, 0);

        return new QuestObjective(min, max, weight, unitWorth);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path, this.serialize());
    }

    @NotNull
    public String serialize() {
        return SectionedData.builder().section(this.min, this.max).section(this.weight).section(this.unitWorth).serialize();
    }

    public int rollAmount(double scale) {
        return (int) Math.ceil(Rnd.get(this.min, this.max) * scale);
    }
}
