package su.nightexpress.quests;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.NightPlugin;
import su.nightexpress.nightcore.commands.command.NightCommand;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.quests.battlepass.BattlePassManager;
import su.nightexpress.quests.command.BaseCommands;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.config.Lang;
import su.nightexpress.quests.config.Perms;
import su.nightexpress.quests.data.DataHandler;
import su.nightexpress.quests.milestone.MilestoneManager;
import su.nightexpress.quests.reward.RewardManager;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.quest.QuestManager;
import su.nightexpress.quests.registry.Registries;
import su.nightexpress.quests.user.UserManager;

import java.util.Optional;

public class QuestsPlugin extends NightPlugin {

    private DataHandler       dataHandler;
    private UserManager       userManager;
    private TaskManager       taskManager;
    private RewardManager     rewardManager;
    private BattlePassManager battlePassManager;
    private MilestoneManager  milestoneManager;
    private QuestManager      questManager;

    @Override
    @NotNull
    protected PluginDetails getDefaultDetails() {
        return PluginDetails.create("Quests", new String[]{"equests", "excellentquests"})
            .setConfigClass(Config.class)
            .setPermissionsClass(Perms.class);
    }

    @Override
    protected void addRegistries() {
        this.registerLang(Lang.class);
    }

    @Override
    protected boolean disableCommandManager() {
        return true;
    }

    @Override
    protected void onStartup() {
        super.onStartup();
        QuestsAPI.load(this);
    }

    @Override
    public void enable() {
        Registries.setup(this);

        this.dataHandler = new DataHandler(this);
        this.dataHandler.setup();

        this.userManager = new UserManager(this, this.dataHandler);
        this.userManager.setup();

        this.taskManager = new TaskManager(this);
        this.taskManager.setup();

        this.rewardManager = new RewardManager(this);
        this.rewardManager.setup();

        if (Config.isBattlePassEnabled()) {
            this.battlePassManager = new BattlePassManager(this);
            this.battlePassManager.setup();
        }

        if (Config.FEATURES_MILESTONES_ENABLED.get()) {
            this.milestoneManager = new MilestoneManager(this);
            this.milestoneManager.setup();
        }

        if (Config.FEATURES_QUESTS_ENABLED.get()) {
            this.questManager = new QuestManager(this);
            this.questManager.setup();
        }

        this.loadCommands();
    }

    @Override
    public void disable() {
        if (this.taskManager != null) this.taskManager.shutdown();
        if (this.milestoneManager != null) this.milestoneManager.shutdown();
        if (this.questManager != null) this.questManager.shutdown();
        if (this.battlePassManager != null) this.battlePassManager.shutdown();
        if (this.rewardManager != null) this.rewardManager.shutdown();
        if (this.userManager != null) this.userManager.shutdown();
        if (this.dataHandler != null) this.dataHandler.shutdown();

        Registries.shutdown();
    }

    @Override
    protected void onShutdown() {
        super.onShutdown();

        QuestsAPI.shutdown();
    }

    private void loadCommands() {
        this.rootCommand = NightCommand.forPlugin(this, builder -> BaseCommands.load(this, builder));
    }

    @NotNull
    public DataHandler getDataHandler() {
        return this.dataHandler;
    }

    @NotNull
    public UserManager getUserManager() {
        return this.userManager;
    }

    @NotNull
    public TaskManager getTaskManager() {
        return this.taskManager;
    }

    @NotNull
    public RewardManager getRewardManager() {
        return this.rewardManager;
    }

    @Nullable
    public BattlePassManager getBattlePassManager() {
        return this.battlePassManager;
    }

    @NotNull
    public Optional<BattlePassManager> battlePassManager() {
        return Optional.ofNullable(this.battlePassManager);
    }

    @Nullable
    public MilestoneManager getMilestoneManager() {
        return this.milestoneManager;
    }

    @NotNull
    public Optional<MilestoneManager> milestoneManager() {
        return Optional.ofNullable(this.milestoneManager);
    }

    @Nullable
    public QuestManager getQuestManager() {
        return this.questManager;
    }

    @NotNull
    public Optional<QuestManager> questManager() {
        return Optional.ofNullable(this.questManager);
    }
}
