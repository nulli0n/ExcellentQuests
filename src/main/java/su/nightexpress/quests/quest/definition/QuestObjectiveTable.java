package su.nightexpress.quests.quest.definition;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.quests.api.AbstractObjectiveTable;
import su.nightexpress.quests.task.adapter.Adapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class QuestObjectiveTable extends AbstractObjectiveTable<QuestObjective> {

    public static final QuestObjectiveTable EMPTY = new QuestObjectiveTable(Collections.emptyMap());

    private static final String ENTRY_DEFINITION_COMMENT = "[MIN];[MAX] [WEIGHT] [UNIT WORTH]";

    public QuestObjectiveTable() {
        super();
    }

    public QuestObjectiveTable(@NotNull Map<String, QuestObjective> entires) {
        super(entires);
    }

    @NotNull
    public static QuestObjectiveTable read(@NotNull FileConfig config, @NotNull String path) {
        Map<String, QuestObjective> entires = new HashMap<>();

        config.getSection(path).forEach(name -> {
            String fullPath = path + "." + name;

            String data = config.getString(fullPath);
            if (data == null) return;

            QuestObjective objective = QuestObjective.deserialize(data);
            entires.put(name, objective);

            config.setInlineComments(fullPath, ENTRY_DEFINITION_COMMENT);
        });

        return new QuestObjectiveTable(entires);
    }

    @NotNull
    public static QuestObjectiveTable.Builder builder() {
        return new QuestObjectiveTable.Builder();
    }

    public static class Builder {

        private final Map<String, QuestObjective> entires;

        private double scale;

        Builder() {
            this.scale = 1D;
            this.entires = new LinkedHashMap<>();
        }

        @NotNull
        public QuestObjectiveTable build() {
            return new QuestObjectiveTable(this.entires);
        }

        @NotNull
        public Builder withScale(double scale) {
            this.scale = scale;
            return this;
        }

        public <I, O, E extends Adapter<I, O>> Builder addEntry(@NotNull String name, @NotNull E adapter, int min, int max, double weight, double unitWorth) {
            I type = adapter.getTypeByName(name);
            if (type == null) return this;

            return this.addType(type, adapter, min, max, weight, unitWorth);
        }

        public <I, O, E extends Adapter<I, O>> Builder addType(@NotNull I type, @NotNull E adapter, int min, int max, double weight, double unitWorth) {
            return this.addEntry(adapter.toFullNameOfType(type), min, max, weight, unitWorth);
        }

        public <I, O, E extends Adapter<I, O>> Builder addEntity(@NotNull O entity, @NotNull E adapter, int min, int max, double weight, double unitWorth) {
            String fullName = adapter.toFullNameOfEntity(entity);
            if (fullName == null) return this;

            return this.addEntry(fullName, min, max, weight, unitWorth);
        }

        @NotNull
        public Builder addEntry(@NotNull String fullName, int min, int max, double weight, double unitWorth) {
            int minScaled = (int) Math.ceil(min * this.scale);
            int maxScaled = (int) Math.ceil(max * this.scale);

            this.entires.put(fullName, new QuestObjective(minScaled, maxScaled, weight, unitWorth));
            return this;
        }
    }
}
