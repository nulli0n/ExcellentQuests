package su.nightexpress.quests.battlepass.definition;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

public class BattlePassLevel implements Writeable {

    private final String[] defaultRewards;
    private final String[] premiumRewards;

    public BattlePassLevel(String[] defaultRewards, String[] premiumRewards) {
        this.defaultRewards = defaultRewards;
        this.premiumRewards = premiumRewards;
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull
    public static BattlePassLevel read(@NotNull FileConfig config, @NotNull String path) {
        String[] defaultRewards = ConfigValue.create(path + ".Default_Rewards", new String[]{}).read(config);
        String[] premiumRewards = ConfigValue.create(path + ".Premium_Rewards", new String[]{}).read(config);

        return new BattlePassLevel(defaultRewards, premiumRewards);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Default_Rewards", String.join(",", this.defaultRewards));
        config.set(path + ".Premium_Rewards", String.join(",", this.premiumRewards));
    }

    public String[] getDefaultRewards() {
        return this.defaultRewards;
    }

    @NotNull
    public String[] getPremiumRewards() {
        return this.premiumRewards;
    }

    public static class Builder {

        private String[] defaultRewards = new String[0];
        private String[] premiumRewards = new String[0];

        Builder() {

        }

        @NotNull
        public BattlePassLevel build() {
            return new BattlePassLevel(this.defaultRewards, this.premiumRewards);
        }

        @NotNull
        public Builder defaultRewards(@NotNull String... rewardIds) {
            return this.rewards(BattlePassType.FREE, rewardIds);
        }

        @NotNull
        public Builder premiumRewards(@NotNull String... rewardIds) {
            return this.rewards(BattlePassType.PREMIUM, rewardIds);
        }

        @NotNull
        public Builder rewards(@NotNull BattlePassType mode, @NotNull String... rewardIds) {
            switch (mode) {
                case FREE -> this.defaultRewards = rewardIds;
                case PREMIUM -> this.premiumRewards = rewardIds;
            }
            return this;
        }
    }
}
