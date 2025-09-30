package su.nightexpress.quests.milestone.definition;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.quests.api.AbstractObjectiveTable;
import su.nightexpress.quests.task.adapter.Adapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MilestoneObjectiveTable extends AbstractObjectiveTable<MilestoneObjective> {

    public static final MilestoneObjectiveTable EMPTY = new MilestoneObjectiveTable(Collections.emptyMap());

    private static final String ENTRY_DEFINITION_COMMENT = "[LV1] [LV2] [LV3] ... [LV999]";

    public MilestoneObjectiveTable() {
        super();
    }

    public MilestoneObjectiveTable(@NotNull Map<String, MilestoneObjective> entires) {
        super(entires);
    }

    @NotNull
    public static MilestoneObjectiveTable read(@NotNull FileConfig config, @NotNull String path) {
        Map<String, MilestoneObjective> entires = new HashMap<>();

        config.getSection(path).forEach(name -> {
            String fullPath = path + "." + name;

            String data = config.getString(fullPath);
            if (data == null) return;

            MilestoneObjective objective = MilestoneObjective.deserialize(data);
            entires.put(name, objective);

            config.setInlineComments(fullPath, ENTRY_DEFINITION_COMMENT);
        });

        return new MilestoneObjectiveTable(entires);
    }

    @NotNull
    public static MilestoneObjectiveTable.Builder builder() {
        return new MilestoneObjectiveTable.Builder();
    }

    public static class Builder {

        private final Map<String, MilestoneObjective> entires;

        Builder() {
            this.entires = new LinkedHashMap<>();
        }

        @NotNull
        public MilestoneObjectiveTable build() {
            return new MilestoneObjectiveTable(this.entires);
        }

        public <I, O, E extends Adapter<I, O>> Builder addEntry(@NotNull String name, @NotNull E adapter, int... amount) {
            I type = adapter.getTypeByName(name);
            if (type == null) return this;

            return this.addType(type, adapter, amount);
        }

        public <I, O, E extends Adapter<I, O>> Builder addType(@NotNull I type, @NotNull E adapter, int... amount) {
            return this.addEntry(adapter.toFullNameOfType(type), amount);
        }

        public <I, O, E extends Adapter<I, O>> Builder addEntity(@NotNull O entity, @NotNull E adapter, int... amount) {
            String fullName = adapter.toFullNameOfEntity(entity);
            if (fullName == null) return this;

            return this.addEntry(fullName, amount);
        }

        @NotNull
        public Builder addEntry(@NotNull String fullName, int... amount) {
            this.entires.put(fullName, new MilestoneObjective(amount));
            return this;
        }
    }
}
