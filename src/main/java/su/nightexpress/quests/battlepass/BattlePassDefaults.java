package su.nightexpress.quests.battlepass;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.battlepass.definition.BattlePassLevel;
import su.nightexpress.quests.battlepass.progression.Progression;
import su.nightexpress.quests.reward.RewardDefaults;

import java.util.Map;
import java.util.TreeMap;

public class BattlePassDefaults {

    @NotNull
    public static Map<Integer, BattlePassLevel> createLevels() {
        Map<Integer, BattlePassLevel> levelMap = new TreeMap<>();

        for (int levelValue = 1; levelValue < Progression.DEFAULT_MAX_LEVEL + 1; levelValue++) {
            if (levelValue % 15 == 0) {
                levelMap.put(levelValue, BattlePassLevel.builder().premiumRewards(RewardDefaults.BP_DIAMONDS).build());
            }
            else if (levelValue % 10 == 0) {
                levelMap.put(levelValue, BattlePassLevel.builder().premiumRewards(RewardDefaults.BP_GOLD_INGOTS).build());
            }
            else if (levelValue % 5 == 0) {
                levelMap.put(levelValue, BattlePassLevel.builder().premiumRewards(RewardDefaults.BP_CASH_MEDIUM).build());
            }
            else if (levelValue % 3 == 0) {
                levelMap.put(levelValue, BattlePassLevel.builder().defaultRewards(RewardDefaults.BP_CASH_LOW).build());
            }
        }

        return levelMap;
    }
}
