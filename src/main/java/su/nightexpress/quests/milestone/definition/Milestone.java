package su.nightexpress.quests.milestone.definition;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.quests.QuestsPlaceholders;
import su.nightexpress.quests.api.IQuest;
import su.nightexpress.quests.api.exception.QuestLoadException;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.registry.Registries;
import su.nightexpress.quests.task.TaskType;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

public class Milestone implements IQuest {

    private final File   file;
    private final String id;

    private TaskType<?, ?>               type;
    private String                       category;
    private String                       name;
    private List<String>                 description;
    private NightItem                    icon;
    private int                          levels;
    private MilestoneObjectiveTable      objectiveTable;
    private List<String> rewards;

    public Milestone(@NotNull File file, @NotNull String id) {
        this.file = file;
        this.id = id;
        this.rewards = new ArrayList<>();
    }

    public void load() throws QuestLoadException {
        FileConfig config = this.getConfig();
        String path = "";

        String typeName = ConfigValue.create(path + ".Type", "null").read(config);
        this.type = Registries.TASK_TYPE.byKey(typeName);
        if (this.type == null) {
            throw new QuestLoadException("Invalid milestone type '" + typeName + "'!");
        }

        this.name = ConfigValue.create(path + ".Name", StringUtil.capitalizeUnderscored(this.id)).read(config);
        this.description = ConfigValue.create(path + ".Description", Collections.emptyList()).read(config);
        this.icon = ConfigValue.create(path + ".Icon", NightItem.fromType(Material.CHEST_MINECART)).read(config);

        this.objectiveTable = MilestoneObjectiveTable.read(config, path + ".Objectives.List");

        this.rewards = ConfigValue.create(path + ".Rewards.Custom", Collections.emptyList()).read(config);

        this.category = ConfigValue.create(path + ".Category", "null").read(config);
        this.levels = ConfigValue.create(path + ".Levels", 1).read(config);

        config.saveChanges();
    }

    public void save() {
        FileConfig config = this.getConfig();
        String path = "";

        config.set(path + ".Type", this.type.getId());
        config.set(path + ".Category", this.category);
        config.set(path + ".Name", this.name);
        config.set(path + ".Description", this.description);
        config.set(path + ".Icon", this.icon);
        config.set(path + ".Levels", this.levels);
        config.set(path + ".Objectives.List", this.objectiveTable);
        config.set(path + ".Rewards.Custom", this.rewards);

        config.save();
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return QuestsPlaceholders.MILESTONE.replacer(this);
    }

    public boolean isCategory(@NotNull MilestoneCategory category) {
        return this.isCategory(category.getId());
    }

    public boolean isCategory(@NotNull String name) {
        return this.category.equalsIgnoreCase(name);
    }

    public int getObjectiveRequirement(@NotNull String fullName, int level) {
        MilestoneObjective objective = this.objectiveTable.getEntry(fullName);
        return objective == null ? -1 : objective.getAmount(level);
    }

    public int countTotalRequirements() {
        int startLevel = Config.isMilestonesResetProgress() ? 1 : this.levels;

        int total = 0;
        for (int level = startLevel; level < this.levels + 1; level++) {
            total += countTotalRequirements(level);
        }
        return total;
    }

    public int countTotalRequirements(int level) {
        return this.objectiveTable.getEntryMap().values().stream().mapToInt(objective -> objective.getAmount(level)).sum();
    }

    @Override
    @NotNull
    public Path getPath() {
        return this.file.toPath();
    }

    @NotNull
    @Override
    public String getId() {
        return this.id;
    }

    @NotNull
    @Override
    public TaskType<?, ?> getType() {
        return this.type;
    }

    public void setType(@NotNull TaskType<?, ?> type) {
        this.type = type;
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public List<String> getDescription() {
        return this.description;
    }

    public void setDescription(@NotNull List<String> description) {
        this.description = description;
    }

    @NotNull
    @Override
    public NightItem getIcon() {
        return this.icon.copy();
    }

    public void setIcon(@NotNull NightItem icon) {
        this.icon = icon.copy();
    }

    @NotNull
    public String getCategory() {
        return this.category;
    }

    public void setCategory(@NotNull String category) {
        this.category = category;
    }

    public int getLevels() {
        return this.levels;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    @NotNull
    public MilestoneObjectiveTable getObjectiveTable() {
        return this.objectiveTable;
    }

    public void setObjectiveTable(@NotNull MilestoneObjectiveTable objectiveTable) {
        this.objectiveTable = objectiveTable;
    }

    @NotNull
    public List<String> getRewards() {
        return this.rewards;
    }

    public void addReward(@NotNull String id) {
        this.rewards.add(id);
    }
}
