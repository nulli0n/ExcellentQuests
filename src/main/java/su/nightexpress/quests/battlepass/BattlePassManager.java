package su.nightexpress.quests.battlepass;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.api.event.battlepass.level.BattlePassLevelDownEvent;
import su.nightexpress.quests.api.event.battlepass.level.BattlePassLevelUpEvent;
import su.nightexpress.quests.api.event.battlepass.season.BattlePassSeasonFinishEvent;
import su.nightexpress.quests.api.event.battlepass.season.BattlePassSeasonLaunchEvent;
import su.nightexpress.quests.battlepass.command.BattlePassCommands;
import su.nightexpress.quests.battlepass.config.BattlePassConfig;
import su.nightexpress.quests.battlepass.data.BattlePassData;
import su.nightexpress.quests.battlepass.definition.BattlePassLevel;
import su.nightexpress.quests.battlepass.definition.BattlePassType;
import su.nightexpress.quests.battlepass.definition.BattlePassSeason;
import su.nightexpress.quests.battlepass.listener.BattlePassListener;
import su.nightexpress.quests.battlepass.menu.BattlePassMenu;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.config.Lang;
import su.nightexpress.quests.config.Perms;
import su.nightexpress.quests.quest.QuestManager;
import su.nightexpress.quests.reward.Reward;
import su.nightexpress.quests.user.QuestUser;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BattlePassManager extends AbstractManager<QuestsPlugin> {

    private static final String FILE_NAME = "battlepass.yml";

    private final Map<UUID, BattlePassSeason> seasonMap;
    private final Map<Integer, BattlePassLevel> levelsMap;

    private BattlePassSeason season;
    private boolean seasonsLoaded;

    private BattlePassMenu battlePassMenu;

    public BattlePassManager(@NotNull QuestsPlugin plugin) {
        super(plugin);
        this.seasonMap = new HashMap<>();
        this.levelsMap = new TreeMap<>();
    }

    @Override
    protected void onLoad() {
        FileConfig config = FileConfig.load(this.plugin.getDataFolder().getAbsolutePath(), FILE_NAME);

        config.initializeOptions(BattlePassConfig.class);
        this.plugin.runTaskAsync(task -> this.loadSeasons());
        this.loadLevels(config);
        this.loadUI();
        this.loadCommands();

        config.saveChanges();

        this.addListener(new BattlePassListener(this.plugin, this));

        this.addTask(this::tickSeasons, 1);
    }

    @Override
    protected void onShutdown() {
        this.seasonsLoaded = false;
        this.seasonMap.clear();
        this.levelsMap.clear();

        BattlePassCommands.shutdown();
    }

    public boolean isSeasonsLoaded() {
        return this.seasonsLoaded;
    }

    public boolean isSeasonActive() {
        return this.season().map(BattlePassSeason::isRunning).orElse(false);
    }

    public boolean isSeasonScheduled() {
        return this.season().map(BattlePassSeason::isScheduled).orElse(false);
    }

    private void loadSeasons() {
        this.plugin.getDataHandler().loadBattlePassSeasons().forEach(this::loadSeason);

        // Set the latest season
        this.setSeason(this.getSeasons().stream().filter(Predicate.not(BattlePassSeason::isExpired)).max(Comparator.comparingLong(BattlePassSeason::getEndDate)).orElse(null));
        this.plugin.info("Loaded " + this.seasonMap.size() + " battle pass season datas.");
        this.seasonsLoaded = true;
    }

    private void loadSeason(@NotNull BattlePassSeason season) {
        this.seasonMap.put(season.getId(), season);
    }

    private void loadLevels(@NotNull FileConfig config) {
        String path = "Levels";
        if (!config.contains(path)) {
            BattlePassDefaults.createLevels().forEach((lvl, level) -> config.set(path + "." + lvl, level));
        }

        config.getSection(path).forEach(sId -> {
            int levelValue = NumberUtil.getIntegerAbs(sId);
            if (levelValue <= 0) return;

            BattlePassLevel level = BattlePassLevel.read(config, path + "." + sId);
            this.levelsMap.put(levelValue, level);
        });

        this.plugin.info("Loaded " + this.levelsMap.size() + " battle pass levels.");
    }

    private void loadUI() {
        this.battlePassMenu = this.addMenu(new BattlePassMenu(this.plugin, this), Config.DIR_MENU, "battlepass_levels.yml");
    }

    private void loadCommands() {
        BattlePassCommands.load(this.plugin, this);
    }

    public void tickSeasons() {
        if (this.season == null) return;

        if (this.season.isLaunched()) {
            if (this.season.isPast()) {
                BattlePassSeasonFinishEvent event = new BattlePassSeasonFinishEvent(this.season);
                this.plugin.getPluginManager().callEvent(event);

                Lang.BATTLE_PASS_SEASON_FINISHED.message().broadcast(replacer -> replacer.replace(this.season.replacePlaceholders()));
                this.season.setLaunched(false);
                this.plugin.runTaskAsync(task -> this.plugin.getDataHandler().saveBattlePassSeason(this.season));
                this.battlePassMenu.flush();
            }
        }
        else if (this.season.isScheduled()) {
            if (this.season.isLaunchTime()) {
                BattlePassSeasonLaunchEvent event = new BattlePassSeasonLaunchEvent(this.season);
                this.plugin.getPluginManager().callEvent(event);

                Lang.BATTLE_PASS_SEASON_LAUNCHED.message().broadcast(replacer -> replacer.replace(this.season.replacePlaceholders()));
                this.season.setLaunched(true);
                this.plugin.runTaskAsync(task -> this.plugin.getDataHandler().saveBattlePassSeason(this.season));
                this.plugin.questManager().ifPresent(QuestManager::updatePlayerQuests);
                this.battlePassMenu.flush();
            }
        }
    }

    public void scheduleSeason(@NotNull CommandSender sender, @NotNull String name, int daysDuration) {
        if (this.isSeasonActive() || this.isSeasonScheduled()) {
            Lang.BATTLE_PASS_SEASON_SCHEDULE_ALREADY.message().send(sender);
            return;
        }

        long startTime = System.currentTimeMillis();
        long endDate = TimeUtil.toEpochMillis(LocalDateTime.of(TimeUtil.getCurrentDate().plusDays(daysDuration), LocalTime.MIDNIGHT));
        long expireDate = endDate + TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS); // TODO Config

        UUID id = UUID.randomUUID();
        BattlePassSeason newSeason = new BattlePassSeason(id, name, startTime, endDate, expireDate, true);
        this.plugin.runTaskAsync(task -> this.plugin.getDataHandler().insertBattlePassSeason(newSeason));

        this.setSeason(newSeason); // Set as current season, as there should be no more than 1 active/scheduled season.
        this.loadSeason(newSeason); // Load it to the map.

        Lang.BATTLE_PASS_SEASON_LAUNCHED.message().broadcast(replacer -> replacer.replace(newSeason.replacePlaceholders()));
    }

    public void cancelSeason(@NotNull CommandSender sender) {
        if (this.season == null || (!this.isSeasonActive() && !this.isSeasonScheduled())) {
            Lang.BATTLE_PASS_SEASON_CANCEL_NOTHING.message().send(sender);
            return;
        }

        BattlePassSeason season = this.season;
        season.setLaunched(false);

        this.plugin.runTaskAsync(task -> this.plugin.getDataHandler().removeBattlePassSeason(season));
        this.seasonMap.remove(season.getId());
        this.setSeason(null);
        this.battlePassMenu.close();
        Lang.BATTLE_PASS_SEASON_CANCELLED.message().send(sender, replacer -> replacer.replace(season.replacePlaceholders()));
    }

    @Nullable
    public BattlePassSeason getSeason() {
        return this.season;
    }

    @NotNull
    public Optional<BattlePassSeason> season() {
        return Optional.ofNullable(this.season);
    }

    public void setSeason(@Nullable BattlePassSeason season) {
        this.season = season;
    }

    @NotNull
    public Map<UUID, BattlePassSeason> getSeasonMap() {
        return this.seasonMap;
    }

    @NotNull
    public Set<BattlePassSeason> getSeasons() {
        return new HashSet<>(this.seasonMap.values());
    }

    @NotNull
    public Map<Integer, BattlePassLevel> getLevelsMap() {
        return this.levelsMap;
    }

    @NotNull
    public Set<BattlePassLevel> getLevels() {
        return new HashSet<>(this.levelsMap.values());
    }

    @Nullable
    public BattlePassLevel getLevel(int level) {
        return this.levelsMap.get(level);
    }

    @NotNull
    public List<Reward> getLevelRewards(int level) {
        BattlePassLevel passLevel = this.getLevel(level);
        if (passLevel == null) return Collections.emptyList();

        List<String> rewardIds = new ArrayList<>();
        rewardIds.addAll(Arrays.asList(passLevel.getDefaultRewards()));
        rewardIds.addAll(Arrays.asList(passLevel.getPremiumRewards()));

        return this.plugin.getRewardManager().parseRewards(rewardIds);
    }

    @NotNull
    public List<Reward> getLevelRewards(int level, @NotNull BattlePassType mode) {
        BattlePassLevel passLevel = this.getLevel(level);
        if (passLevel == null) return Collections.emptyList();

        List<String> rewardIds = Arrays.asList(mode == BattlePassType.FREE ? passLevel.getDefaultRewards() : passLevel.getPremiumRewards());

        return this.plugin.getRewardManager().parseRewards(rewardIds);
    }

    public void openBattlePass(@NotNull Player player) {
        if (this.season == null) {
            Lang.BATTLE_PASS_NO_ACTIVE_SEASON.message().send(player);
            return;
        }
        this.battlePassMenu.openAtLevel(player, this.season);
    }

    public void checkPremiums() {
        if (!BattlePassConfig.SETTINGS_PREMIUM_BY_PERMISSION.get()) return;

        Players.getOnline().forEach(this::checkPremium);
    }

    public void checkPremium(@NotNull Player player) {
        if (!BattlePassConfig.SETTINGS_PREMIUM_BY_PERMISSION.get()) return;

        this.setPremium(player, player.hasPermission(Perms.BATTLE_PASS_PREMIUM));
    }

    public void setPremium(@NotNull Player player, boolean flag) {
        QuestUser user = this.plugin.getUserManager().getOrFetch(player);
        this.setPremium(user, flag);
    }

    public void setPremium(@NotNull QuestUser user, boolean flag) {
        if (this.season == null) return;
        if (!this.isSeasonActive() && !this.isSeasonScheduled()) return;

        BattlePassData data = user.getBattlePassData(this.season);
        if (data.isPremium() != flag) {
            data.setPremium(flag);
            this.plugin.getUserManager().save(user);
        }
    }

    public boolean addLevel(@NotNull Player player, int amount) {
        QuestUser user = plugin.getUserManager().getOrFetch(player);
        return this.handleLevelAdd(user, amount);
    }

    public boolean setLevel(@NotNull Player player, int amount) {
        QuestUser user = plugin.getUserManager().getOrFetch(player);
        return this.handleLevelSet(user, amount);
    }

    public boolean removeLevel(@NotNull Player player, int amount) {
        QuestUser user = plugin.getUserManager().getOrFetch(player);
        return this.handleLevelRemove(user, amount);
    }

    public boolean addXP(@NotNull Player player, int amount) {
        QuestUser user = plugin.getUserManager().getOrFetch(player);
        return this.handleXPAdd(user, amount);
    }

    public boolean setXP(@NotNull Player player, int amount) {
        QuestUser user = plugin.getUserManager().getOrFetch(player);
        return this.handleXPSet(user, amount);
    }

    public boolean removeXP(@NotNull Player player, int amount) {
        QuestUser user = plugin.getUserManager().getOrFetch(player);
        return this.handleXPRemove(user, amount);
    }



    public boolean handleLevelAdd(@NotNull QuestUser user, int amount) {
        if (this.season == null) return false;

        BattlePassData data = user.getBattlePassData(this.season);
        if (!data.isMaxLevel()) {
            return this.handleLevelSet(user, data.getLevel() + amount);
        }
        return false;
    }

    public boolean handleLevelRemove(@NotNull QuestUser user, int amount) {
        if (this.season == null) return false;

        BattlePassData data = user.getBattlePassData(this.season);
        if (!data.isStartLevel()) {
            return this.handleLevelSet(user, data.getLevel() - amount);
        }
        return false;
    }

    public boolean handleLevelSet(@NotNull QuestUser user, int amount) {
        return this.handleDataXPChange(user, data -> {
            data.setLevel(amount);
            data.setXP(0);
        });
    }

    public boolean handleXPAdd(@NotNull QuestUser user, int amount) {
        if (this.season == null) return false;

        BattlePassData data = user.getBattlePassData(this.season);
        return this.handleXPSet(user, data.getXP() + amount);
    }

    public boolean handleXPRemove(@NotNull QuestUser user, int amount) {
        if (this.season == null) return false;

        BattlePassData data = user.getBattlePassData(this.season);
        return this.handleXPSet(user, data.getXP() - amount);
    }

    public boolean handleXPSet(@NotNull QuestUser user, int amount) {
        return this.handleDataXPChange(user, data -> {
            data.setXP(amount);
        });
    }

    private boolean handleDataXPChange(@NotNull QuestUser user, @NotNull Consumer<BattlePassData> consumer) {
        if (this.season == null) return false;

        BattlePassData data = user.getBattlePassData(this.season);
        if (!data.canProgress()) return false;

        int oldLevel = data.getLevel();

        consumer.accept(data);
        data.update();
        plugin.getUserManager().save(user);

        Player player = user.getPlayer();
        if (player != null) {
            if (data.getLevel() > oldLevel) {
                this.handleLevelUp(player, data, oldLevel);
            }
            else if (data.getLevel() < oldLevel) {
                this.handleLevelDown(player, data, oldLevel);
            }
        }

        return true;
    }

    private void handleLevelUp(@NotNull Player player, @NotNull BattlePassData data, int oldLevel) {
        QuestUser user = plugin.getUserManager().getOrFetch(player);

        BattlePassLevelUpEvent event = new BattlePassLevelUpEvent(player, user, data, oldLevel);
        plugin.getPluginManager().callEvent(event);

        Lang.BATTLE_PASS_LEVEL_UP.message().send(player, replacer -> replacer.replace(data.replacePlaceholders()));
    }

    private void handleLevelDown(@NotNull Player player, @NotNull BattlePassData data, int oldLevel) {
        QuestUser user = plugin.getUserManager().getOrFetch(player);

        BattlePassLevelDownEvent event = new BattlePassLevelDownEvent(player, user, data, oldLevel);
        plugin.getPluginManager().callEvent(event);

        Lang.BATTLE_PASS_LEVEL_DOWN.message().send(player, replacer -> replacer.replace(data.replacePlaceholders()));
    }
}
