package su.nightexpress.quests.quest;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.quests.QuestsPlaceholders;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.api.exception.QuestLoadException;
import su.nightexpress.quests.battlepass.BattlePassManager;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.config.Lang;
import su.nightexpress.quests.quest.command.QuestsCommands;
import su.nightexpress.quests.quest.data.QuestData;
import su.nightexpress.quests.quest.definition.Quest;
import su.nightexpress.quests.quest.listener.QuestGenericListener;
import su.nightexpress.quests.quest.menu.QuestsMenu;
import su.nightexpress.quests.reward.Reward;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.user.QuestUser;
import su.nightexpress.quests.util.QuestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class QuestManager extends AbstractManager<QuestsPlugin> {

    private final Map<String, Quest> questById;
    private final String dirPath;

    private QuestsMenu questsMenu;

    public QuestManager(@NotNull QuestsPlugin plugin) {
        super(plugin);
        this.questById = new HashMap<>();
        this.dirPath = this.plugin.getDataFolder() + Config.DIR_QUESTS;
    }

    @Override
    protected void onLoad() {
        this.loadQuests();
        this.loadUI();

        this.addListener(new QuestGenericListener(this.plugin, this));
        this.addAsyncTask(this::updatePlayerQuests, 1);

        QuestsCommands.load(this.plugin, this);
    }

    @Override
    protected void onShutdown() {
        this.questById.clear();

        QuestsCommands.shutdown();
    }

    private void loadQuests() {
        File dir = new File(this.dirPath);
        if (!dir.exists() && dir.mkdirs()) {
            QuestDefaults.createQuests(this);
        }

        FileUtil.getConfigFiles(this.dirPath).forEach(file -> {
            String id = Strings.varStyle(FileConfig.getName(file)).orElse(null);
            if (id == null) {
                this.plugin.error("Could not load quest '" + file.getPath() + "' due to malformed file name.");
                return;
            }

            Quest quest = new Quest(file, id);
            try {
                quest.load();
            }
            catch (QuestLoadException exception) {
                this.plugin.error("Quest '" + file.getPath() + "' not loaded: " + exception.getMessage());
                return;
            }

            this.questById.put(quest.getId(), quest);
        });

        this.plugin.info("Loaded " + this.questById.size() + " quests.");
    }

    private void loadUI() {
        this.questsMenu = this.addMenu(new QuestsMenu(this.plugin, this), Config.DIR_MENU, "quests.yml");
    }

    public boolean isQuestsAvailable() {
        if (Config.isQuestsForBattlePass()) {
            return this.plugin.battlePassManager().map(BattlePassManager::isSeasonActive).orElse(false);
        }

        return true;
    }

    public void createQuest(@NotNull String name, @NotNull Consumer<Quest> consumer) {
        String id = Strings.varStyle(name).orElseThrow(() -> new IllegalArgumentException("Invalid quest ID '" + name + "'"));
        if (this.getQuestById(id) != null) return;

        File file = new File(this.dirPath, FileConfig.withExtension(id));
        Quest quest = new Quest(file, id);
        consumer.accept(quest);
        quest.save();
    }

    public void openQuests(@NotNull Player player) {
        if (!this.isQuestsAvailable()) {
            Lang.QUESTS_LOCKED.message().send(player);
            return;
        }
        this.questsMenu.open(player);
    }

    public void refreshQuests(@NotNull Player player) {
        QuestUser user = this.plugin.getUserManager().getOrFetch(player);
        user.setNewQuestsDate(0L);
        this.updatePlayerQuests(player);
    }

    public void updatePlayerQuests() {
        Players.getOnline().forEach(this::updatePlayerQuests);
    }

    public void updatePlayerQuests(@NotNull Player player) {
        QuestUser user = this.plugin.getUserManager().getOrFetch(player);

        // Notify exprired quests.
        user.getQuestDatas().forEach(questData -> {
            if (!questData.isActive()) return;
            if (!questData.isExpired()) return;

            Quest quest = this.getQuestById(questData.getQuestId());
            if (quest == null) return;

            Lang.QUESTS_QUEST_TIME_OUT.message().send(player, replacer -> replacer.replace(quest.replacePlaceholders()));

            questData.setActive(false);
        });

        if (!this.isQuestsAvailable()) return;
        if (!user.isNewQuestsTime()) return;

        user.clearQuestData();

        int maxQuests = Config.QUESTS_AMOUT_PER_RANK.get().getGreatest(player).intValue();
        if (maxQuests <= 0) return;

        long nextQuestsDate = TimeUtil.toEpochMillis(QuestUtils.getNewDayMidnight());
        boolean acceptionRequired = Config.QUESTS_ACCEPTION_REQUIRED.get();
        boolean autoCompletionTime = Config.QUESTS_AUTO_COMPLETION_TIME.get();

        List<Quest> quests = new ArrayList<>(this.questById.values());
        while (maxQuests > 0 && !quests.isEmpty()) {
            Quest quest = quests.remove(Rnd.nextInt(quests.size()));

            QuestData questData = quest.createQuestData();
            if (questData == null) return;

            if (!acceptionRequired) {
                questData.setActive(true);
                questData.setExpireDate(TimeUtil.createFutureTimestamp(quest.getCompletionTime()));
            }
            if (autoCompletionTime) {
                questData.setExpireDate(nextQuestsDate);
            }

            user.addQuestData(questData);

            maxQuests--;
        }

        int count = user.countQuestsAmount();
        Lang.QUESTS_REFRESHED.message().send(player, replacer -> replacer.replace(QuestsPlaceholders.GENERIC_AMOUNT, String.valueOf(count)));

        user.setNewQuestsDate(nextQuestsDate);

        if (this.questsMenu.isViewer(player)) {
            this.plugin.runTask(() -> this.questsMenu.flush(player)); // Back to the main thread for GUI update.
        }

        this.plugin.getUserManager().save(user);
    }

    public <O, A extends AdapterFamily<O>> void progressQuests(@NotNull Player player, @NotNull TaskType<O, A> taskType, @NotNull String fullName, int amount) {
        if (!this.isQuestsAvailable()) return;

        QuestUser user = this.plugin.getUserManager().getOrFetch(player);

        for (QuestData questData : user.getQuestDatas()) {
            if (!questData.isActive()) continue;
            if (questData.isCompleted()) continue;
            if (questData.isExpired()) continue;

            Quest quest = this.getQuestById(questData.getQuestId());
            if (quest == null) continue;

            if (quest.getType() != taskType) continue;

            int required = questData.getRequired(fullName);
            if (required <= 0) continue;

            int count = Math.min(required, amount);

            questData.addCompleted(fullName, count);

            if (questData.isCompleted()) {
                List<Reward> rewards = this.plugin.getRewardManager().getQuestRewards(quest);
                boolean hasRewards = !rewards.isEmpty();
                double scale = questData.getScale();
                int units = questData.countUnitsWorth();
                int xpReward = questData.getXPReward();

                rewards.forEach(reward -> reward.runCommands(player, units, 0, scale));

                (hasRewards ? Lang.QUESTS_QUEST_COMPLETED_XP_REWARDS : Lang.QUESTS_QUEST_COMPLETED_XP_ONLY).message().send(player, replacer -> replacer
                    .replace(quest.replacePlaceholders())
                    .replace(QuestsPlaceholders.GENERIC_XP, NumberUtil.format(xpReward))
                    .replace(QuestsPlaceholders.GENERIC_REWARDS, () -> rewards.stream().map(reward -> reward.getName(units, 0, scale)).collect(Collectors.joining(", ")))
                );

                this.plugin.battlePassManager().ifPresent(bp -> bp.addXP(player, xpReward));
            }
        }
    }
    
    @Nullable
    public Quest getQuestById(@NotNull String id) {
        return this.questById.get(id);
    }
}
