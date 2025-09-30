package su.nightexpress.quests.battlepass.data;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.quests.QuestsPlaceholders;
import su.nightexpress.quests.battlepass.config.BattlePassConfig;
import su.nightexpress.quests.battlepass.definition.BattlePassType;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.battlepass.progression.Progression;

import java.util.*;
import java.util.function.UnaryOperator;

public class BattlePassData {

    private final Progression                       progression;
    private final Map<BattlePassType, Set<Integer>> claimedRewards;
    private final long                              expireDate;

    private int     level;
    private int     xp;
    private boolean premium;

    public BattlePassData(int xp, int level, boolean premium, @NotNull Map<BattlePassType, Set<Integer>> claimedRewards, long expireDate) {
        this.progression = Config.isBattlePassEnabled() ? BattlePassConfig.LEVELING_PROGRESSION.get() : Progression.noProgression();
        this.xp = xp;
        this.level = level;
        this.setPremium(premium);
        this.claimedRewards = claimedRewards;
        this.expireDate = expireDate;
    }

    @NotNull
    public static BattlePassData create(long expireDate) {
        return new BattlePassData(0, Progression.START_LEVEL, false, new HashMap<>(), expireDate);
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return QuestsPlaceholders.BATTLE_PASS_DATA.replacer(this);
    }

    public boolean isExpired() {
        return TimeUtil.isPassed(this.expireDate);
    }

    public void reset() {
        this.reset(false);
    }

    public void reset(boolean full) {
        if (!this.progression.canProgress()) return;

        this.setXP(0);
        this.setLevel(Progression.START_LEVEL);

        if (full) {
            this.claimedRewards.clear();
        }
    }

    public void update() {
        if (!this.progression.canProgress()) return;

        int maxLevel = this.progression.getMaxLevel();
        if (this.level > maxLevel) {
            this.setLevel(maxLevel);
            this.setXP(this.getLevelXP());
        }

        this.checkXPLevelUp();
        this.checkXPLevelDown();
    }

    public boolean canProgress() {
        return this.progression.canProgress();
    }

    public void removeXP(int amount) {
        this.setXP(this.xp - Math.abs(amount));
        this.checkXPLevelDown();
    }

    public void addXP(int amount) {
        this.setXP(this.xp + Math.abs(amount));
        this.checkXPLevelUp();
    }

    private void upLevel(int leftover) {
        this.setLevel(this.level + 1);
        this.setXP(leftover);
        this.checkXPLevelUp();
    }

    private void downLevel(int leftover) {
        if (this.isStartLevel()) return;

        this.setLevel(this.level - 1);

        int levelXP = this.getLevelXP();

        this.setXP(levelXP + leftover);
        this.checkXPLevelDown();
    }

    private void checkXPLevelUp() {
        int levelXP = this.getLevelXP();
        if (this.xp < levelXP) return;

        if (this.isMaxLevel()) {
            this.setXP(levelXP);
        }
        else {
            this.upLevel(this.xp - levelXP);
        }
    }

    private void checkXPLevelDown() {
        int levelAntiXP = -this.getLevelXP();
        if (this.xp > levelAntiXP) return;

        if (this.isStartLevel()) {
            this.setXP(levelAntiXP);
        }
        else {
            this.downLevel(this.xp - levelAntiXP);
        }
    }

    public boolean isStartLevel() {
        return this.level == Progression.START_LEVEL;
    }

    public boolean isMaxLevel() {
        return this.level == this.getMaxLevel();
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = Math.max(Progression.START_LEVEL, Math.min(level, this.getMaxLevel()));
    }

    public int getMaxLevel() {
        return this.progression.getMaxLevel();
    }

    public int getXP() {
        return this.xp;
    }

    public void setXP(int xp) {
        this.xp = xp;
    }

    public int getLevelXP() {
        return this.progression.getXPToLevel(this.level);
    }

    public int getXPToLevelUp() {
        return this.getLevelXP() - this.xp;
    }

    public int getXPToLevelDown() {
        return this.xp + this.getLevelXP();
    }

    public boolean isPremium() {
        return this.premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public long getExpireDate() {
        return this.expireDate;
    }

    @NotNull
    public Map<BattlePassType, Set<Integer>> getClaimedRewards() {
        return this.claimedRewards;
    }

    @NotNull
    public Set<Integer> getClaimedLevelRewards(@NotNull BattlePassType mode) {
        return this.claimedRewards.getOrDefault(mode, Collections.emptySet());
    }

    public boolean isRewardClaimed(@NotNull BattlePassType mode, int level) {
        return this.getClaimedLevelRewards(mode).contains(level);
    }

    public void setRewardClaimed(@NotNull BattlePassType mode, int level) {
        this.claimedRewards.computeIfAbsent(mode, k -> new HashSet<>()).add(level);
    }
}
