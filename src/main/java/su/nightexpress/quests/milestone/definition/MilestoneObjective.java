package su.nightexpress.quests.milestone.definition;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.quests.util.SectionedData;

public record MilestoneObjective(int[] amounts) implements Writeable {

    @NotNull
    public static MilestoneObjective deserialize(@NotNull String rawData) {
        SectionedData data = SectionedData.deserialize(rawData);

        int length = data.sectionsLength();
        int[] amounts = new int[length];

        for (int index = 0; index < length; index++) {
            amounts[index] = data.getInt(index, 0, 0);
        }

        return new MilestoneObjective(amounts);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path, this.serialize());
    }

    @NotNull
    public String serialize() {
        SectionedData.Builder builder = SectionedData.builder();
        for (int amount : this.amounts) {
            builder.section(amount);
        }
        return builder.serialize();
    }

    public int getAmount(int level) {
        int index = Math.max(0, level - 1);

        return index >= this.amounts.length ? 0 : this.amounts[index];
    }
}
