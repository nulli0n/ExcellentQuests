package su.nightexpress.quests.milestone;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Strings;
import su.nightexpress.quests.QuestsPlaceholders;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.api.exception.QuestLoadException;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.config.Lang;
import su.nightexpress.quests.milestone.command.MilestoneCommands;
import su.nightexpress.quests.milestone.data.MilestoneData;
import su.nightexpress.quests.milestone.definition.Milestone;
import su.nightexpress.quests.milestone.definition.MilestoneCategory;
import su.nightexpress.quests.milestone.listener.MilestoneGenericListener;
import su.nightexpress.quests.milestone.menu.CategoriesMenu;
import su.nightexpress.quests.milestone.menu.MilestonesMenu;
import su.nightexpress.quests.milestone.menu.ProgressionMenu;
import su.nightexpress.quests.reward.Reward;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.user.QuestUser;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MilestoneManager extends AbstractManager<QuestsPlugin> {

    private final Map<String, MilestoneCategory> categoryById;
    private final Map<String, Milestone>         milestoneById;
    private final String dirPath;

    private CategoriesMenu categoriesMenu;
    private MilestonesMenu milestonesMenu;
    private ProgressionMenu progressionMenu;

    public MilestoneManager(@NotNull QuestsPlugin plugin) {
        super(plugin);
        this.dirPath = plugin.getDataFolder() + Config.DIR_MILESTONES;
        this.categoryById = new HashMap<>();
        this.milestoneById = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        FileConfig config = this.plugin.getConfig();
        this.loadCategories(config);
        this.loadMilestones();
        this.loadUI();
        this.loadCommands();

        this.addListener(new MilestoneGenericListener(this.plugin, this));

        // Add new milestones for online players after the config reload.
        this.plugin.runTaskAsync(task -> {
            Players.getOnline().forEach(this::updateMilestones);
        });
    }

    @Override
    protected void onShutdown() {
        this.categoryById.clear();
        this.milestoneById.clear();

        MilestoneCommands.shutdown();
    }

    private void loadCategories(@NotNull FileConfig config) {
        String path = "Milestones.Categories";
        if (!config.contains(path)) {
            MilestoneDefaults.createCategories().forEach(category -> config.set(path + "." + category.getId(), category));
        }

        config.getSection(path).forEach(sId -> {
            MilestoneCategory category = MilestoneCategory.read(config, path + "." + sId, sId);
            this.categoryById.put(category.getId(), category);
        });

        this.plugin.info("Loaded " + this.categoryById.size() + " milestone categories.");
    }

    private void loadMilestones() {
        File dir = new File(this.dirPath);
        if (!dir.exists() && dir.mkdirs()) {
            MilestoneDefaults.createMilestones(this);
        }

        FileUtil.getConfigFiles(this.dirPath).forEach(file -> {
            String id = Strings.filterForVariable(FileConfig.getName(file));

            Milestone milestone = new Milestone(file, id);
            try {
                milestone.load();
            }
            catch (QuestLoadException exception) {
                this.plugin.error("Quest '" + file.getPath() + "' not loaded: " + exception.getMessage());
                return;
            }

            this.milestoneById.put(milestone.getId(), milestone);
        });

        this.plugin.info("Loaded " + this.milestoneById.size() + " milestones.");
    }

    private void loadUI() {
        this.categoriesMenu = this.addMenu(new CategoriesMenu(this.plugin, this), Config.DIR_MENU, "milestone_categories.yml");
        this.milestonesMenu = this.addMenu(new MilestonesMenu(this.plugin, this), Config.DIR_MENU, "milestones.yml");
        this.progressionMenu = this.addMenu(new ProgressionMenu(this.plugin, this), Config.DIR_MENU, "milestone_progression.yml");
    }

    private void loadCommands() {
        MilestoneCommands.load(this.plugin, this);
    }

    public void createMilestone(@NotNull String name, @NotNull Consumer<Milestone> consumer) {
        String id = Strings.filterForVariable(name);
        if (this.getMilestoneById(id) != null) return;

        File file = new File(this.dirPath, FileConfig.withExtension(id));
        Milestone milestone = new Milestone(file, id);
        consumer.accept(milestone);
        milestone.save();
    }

    public void openCategories(@NotNull Player player) {
        this.categoriesMenu.open(player);
    }

    public void openMilestones(@NotNull Player player, @NotNull MilestoneCategory category) {
        this.milestonesMenu.open(player, category);
    }

    public void openProgression(@NotNull Player player, @NotNull Milestone milestone) {
        this.progressionMenu.open(player, milestone);
    }

    public void updateMilestones(@NotNull Player player) {
        QuestUser user = this.plugin.getUserManager().getOrFetch(player);

        boolean result = this.getMilestones().stream().anyMatch(user::addMilestone);
        if (result) {
            this.plugin.getUserManager().save(user);
        }
    }

    public <O, A extends AdapterFamily<O>> void progressMilestones(@NotNull Player player, @NotNull TaskType<O, A> taskType, @NotNull String fullName, int amount) {
        QuestUser user = this.plugin.getUserManager().getOrFetch(player);

        // TODO Get milestones by mission type
        this.getMilestones().forEach(milestone -> {
            if (milestone.getType() != taskType) return;
            if (user.isCompleted(milestone)) return;

            MilestoneData data = user.getMilestoneData(milestone);

            int level = data.getFirstIncompletedLevel(milestone);
            if (level <= 0) return;

            int required = milestone.getObjectiveRequirement(fullName, level);
            if (required <= 0) return;

            int progress = data.getObjectiveProgress(fullName);
            if (progress >= required) return;

            int total = Math.min(required, progress + amount);

            data.setObjectiveProgress(fullName, total);

            if (data.isReady(milestone, level)) {
                data.addCompletedLevel(level);

                List<Reward> rewards = this.plugin.getRewardManager().getMilestoneRewards(milestone);
                int units = data.countTotalProgress(milestone);

                rewards.forEach(milestoneReward -> milestoneReward.runCommands(player, units, level, 1D));

                Lang.MILESTONES_MILESTONE_COMPLETED.message().send(player, replacer -> replacer
                    .replace(milestone.replacePlaceholders())
                    .replace(QuestsPlaceholders.GENERIC_LEVEL, String.valueOf(level))
                    .replace(QuestsPlaceholders.GENERIC_REWARDS, rewards.stream().map(reward -> reward.getName(units, level, 1D)).collect(Collectors.joining(", ")))
                );
            }
        });
    }

    @NotNull
    public Set<Milestone> getMilestonesByCategory(@NotNull MilestoneCategory category) {
        return this.getMilestones().stream().filter(milestone -> milestone.isCategory(category)).collect(Collectors.toSet());
    }

    @NotNull
    public Map<String, MilestoneCategory> getCategoryByIdMap() {
        return this.categoryById;
    }

    @NotNull
    public Set<MilestoneCategory> getCategories() {
        return new HashSet<>(this.categoryById.values());
    }

    @Nullable
    public MilestoneCategory getCategoryById(@NotNull String id) {
        return this.categoryById.get(id);
    }

    @NotNull
    public Map<String, Milestone> getMilestoneByIdMap() {
        return this.milestoneById;
    }

    @Nullable
    public Milestone getMilestoneById(@NotNull String id) {
        return this.milestoneById.get(id);
    }

    @NotNull
    public Set<Milestone> getMilestones() {
        return new HashSet<>(this.milestoneById.values());
    }
}
