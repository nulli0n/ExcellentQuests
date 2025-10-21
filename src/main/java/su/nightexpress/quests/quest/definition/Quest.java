package su.nightexpress.quests.quest.definition;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.nightcore.util.wrapper.UniInt;
import su.nightexpress.quests.QuestsPlaceholders;
import su.nightexpress.quests.api.IQuest;
import su.nightexpress.quests.api.exception.QuestLoadException;
import su.nightexpress.quests.quest.data.QuestCounter;
import su.nightexpress.quests.quest.data.QuestData;
import su.nightexpress.quests.registry.Registries;
import su.nightexpress.quests.task.TaskType;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.UnaryOperator;

public class Quest implements IQuest {

    private final File   file;
    private final String id;

    private TaskType<?, ?>      type;
    private String              name;
    private List<String>        description;
    private NightItem           icon;
    private UniInt              objectivesAmount;
    private QuestObjectiveTable objectiveTable;
    private List<String>        rewards;
    private QuestXPReward       battlePassXPReward;

    private long completionTime;

    public Quest(@NotNull File file, @NotNull String id) {
        this.file = file;
        this.id = id;
        this.rewards = new ArrayList<>();
    }

    @Override
    public void load() throws QuestLoadException {
        FileConfig config = this.loadConfig();
        String path = "";

        String typeName = ConfigValue.create(path + ".Type", "null").read(config);
        this.type = Registries.TASK_TYPE.byKey(typeName);
        if (this.type == null) {
            throw new QuestLoadException("Invalid quest type '" + typeName + "'!");
        }

        this.name = ConfigValue.create(path + ".Name", StringUtil.capitalizeUnderscored(this.id)).read(config);
        this.description = ConfigValue.create(path + ".Description", Collections.emptyList()).read(config);
        this.icon = ConfigValue.create(path + ".Icon", NightItem.fromType(Material.CHEST_MINECART)).read(config);

        this.objectivesAmount = ConfigValue.create(path + ".Objectives.Amount", UniInt::read, UniInt.of(1, -1)).read(config);
        this.objectiveTable = QuestObjectiveTable.read(config, path + ".Objectives.List");

        this.rewards = ConfigValue.create(path + ".Rewards.Custom", Collections.emptyList()).read(config);

        this.setBattlePassXPReward(QuestXPReward.read(config, path + ".Rewards.BattlePassXP"));

        this.completionTime = ConfigValue.create(path + ".CompletionTime", 7200).read(config);

        config.saveChanges();
    }

    public void save() {
        FileConfig config = this.loadConfig();
        String path = "";

        config.set(path + ".Type", this.type.getId());
        config.set(path + ".Name", this.name);
        config.set(path + ".Description", this.description);
        config.set(path + ".Icon", this.icon);
        config.set(path + ".Objectives.Amount", this.objectivesAmount);
        config.set(path + ".Objectives.List", this.objectiveTable);
        config.set(path + ".Rewards.BattlePassXP", this.battlePassXPReward);
        config.set(path + ".Rewards.Custom", this.rewards);
        config.set(path + ".CompletionTime", this.completionTime);

        config.save();
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return QuestsPlaceholders.QUEST.replacer(this);
    }

    @Nullable
    public QuestData createQuestData() {
        UUID uuid = UUID.randomUUID();
        Map<String, QuestCounter> objectives = new LinkedHashMap<>();
        Set<String> rewardIds = new HashSet<>(this.rewards);

        double scale = 1D; // TODO

        Map<String, Double> objectiveByWeight = new HashMap<>();
        this.objectiveTable.getEntryMap().forEach((fullName, objective) -> {
            objectiveByWeight.put(fullName, objective.weight());
        });

        double unitsWorth = 0;
        int objectivesAmount = this.objectivesAmount.roll();
        while (objectivesAmount > 0 && !objectiveByWeight.isEmpty()) {
            String fullName = Rnd.getByWeight(objectiveByWeight);
            objectiveByWeight.remove(fullName);

            QuestObjective objective = this.objectiveTable.getEntry(fullName);
            if (objective == null) continue;

            int amount = objective.rollAmount(scale);
            if (amount <= 0) continue;

            double unitWorth = objective.unitWorth();

            objectives.put(LowerCase.INTERNAL.apply(fullName), QuestCounter.create(amount, unitWorth));
            objectivesAmount--;
            unitsWorth += (amount * unitWorth);
        }
        if (objectives.isEmpty()) return null;

        /*Map<String, Double> rewardByWeight = new HashMap<>();
        this.rewards.forEach((id, reward) -> {
            rewardByWeight.put(id, reward.getWeight());
        });

        if (!rewardByWeight.isEmpty()) {
            rewardIds.add(Rnd.getByWeight(rewardByWeight));
        }*/

        int xpReward = this.battlePassXPReward.getXP(unitsWorth);

        boolean active = false;
        long expireDate = -1L;

        return new QuestData(uuid, this.id, objectives, rewardIds, scale, xpReward, active, expireDate);
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
    public UniInt getObjectivesAmount() {
        return this.objectivesAmount;
    }

    public void setObjectivesAmount(@NotNull UniInt objectivesAmount) {
        this.objectivesAmount = objectivesAmount;
    }

    @NotNull
    public QuestObjectiveTable getObjectiveTable() {
        return this.objectiveTable;
    }

    public void setObjectiveTable(@NotNull QuestObjectiveTable objectiveTable) {
        this.objectiveTable = objectiveTable;
    }

    public long getCompletionTime() {
        return this.completionTime;
    }

    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    @NotNull
    public QuestXPReward getBattlePassXPReward() {
        return this.battlePassXPReward;
    }

    public void setBattlePassXPReward(double base, double unitBonus) {
        this.setBattlePassXPReward(new QuestXPReward(base, unitBonus));
    }

    public void setBattlePassXPReward(@NotNull QuestXPReward xpReward) {
        this.battlePassXPReward = xpReward;
    }

    @NotNull
    public List<String> getRewards() {
        return this.rewards;
    }

    public void addReward(@NotNull String rewardId) {
        this.rewards.add(rewardId);
    }
}
