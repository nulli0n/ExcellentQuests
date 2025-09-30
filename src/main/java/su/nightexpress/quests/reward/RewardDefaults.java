package su.nightexpress.quests.reward;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import static su.nightexpress.quests.QuestsPlaceholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class RewardDefaults {

    public static final String MS_CASH_LOW    = "ms_cash_low";
    public static final String MS_CASH_MEDIUM = "ms_cash_medium";
    public static final String MS_CASH_HIGH   = "ms_cash_high";

    public static final String QUEST_CASH_LOW    = "quest_cash_low";
    public static final String QUEST_CASH_MEDIUM = "quest_cash_medium";
    public static final String QUEST_CASH_HIGH   = "quest_cash_high";

    public static final String BP_CASH_LOW    = "bp_cash_low";
    public static final String BP_CASH_MEDIUM = "bp_cash_medium";
    public static final String BP_CASH_HIGH   = "bp_cash_high";
    public static final String BP_GOLD_INGOTS = "bp_gold_ingots";
    public static final String BP_DIAMONDS    = "bp_diamonds";

    private static final double[] MS_CASH_LOW_VALUES    = new double[]{150, 300, 500, 750, 1500, 3000, 5000, 7500, 10_000};
    private static final double[] MS_CASH_MEDIUM_VALUES = new double[]{500, 750, 1000, 1500, 3000, 5000, 7500, 10_000, 15_000};
    private static final double[] MS_CASH_HIGH_VALUES   = new double[]{1000, 1500, 2750, 4500, 7000, 10_000, 15_000, 20_000, 25_000};

    private static final String VAR_AMOUNT_NAME = "amount";
    private static final String VAR_AMOUNT_FULL = VARIABLE.apply(VAR_AMOUNT_NAME);

    @NotNull
    public static Map<String, Reward> createRewards() {
        Map<String, Reward> rewards = new LinkedHashMap<>();

        // For milestones only (0 base 0 per unit, level bonus only)
        rewards.put(MS_CASH_LOW, Reward.builder("$" + VAR_AMOUNT_FULL)
            .commands("money give " + PLAYER_NAME + " " + VAR_AMOUNT_FULL)
            .variable(VAR_AMOUNT_NAME, 0, 0, MS_CASH_LOW_VALUES)
            .build()
        );

        rewards.put(MS_CASH_MEDIUM, Reward.builder("$" + VAR_AMOUNT_FULL)
            .commands("money give " + PLAYER_NAME + " " + VAR_AMOUNT_FULL)
            .variable(VAR_AMOUNT_NAME, 0, 0, MS_CASH_MEDIUM_VALUES)
            .build()
        );

        rewards.put(MS_CASH_HIGH, Reward.builder("$" + VAR_AMOUNT_FULL)
            .commands("money give " + PLAYER_NAME + " " + VAR_AMOUNT_FULL)
            .variable(VAR_AMOUNT_NAME, 0, 0, MS_CASH_HIGH_VALUES)
            .build()
        );

        // For quests
        rewards.put(QUEST_CASH_LOW, Reward.builder("$" + VAR_AMOUNT_FULL)
            .commands("money give " + PLAYER_NAME + " " + VAR_AMOUNT_FULL)
            .variable(VAR_AMOUNT_NAME, 250, 3.5)
            .build()
        );

        rewards.put(QUEST_CASH_MEDIUM, Reward.builder("$" + VAR_AMOUNT_FULL)
            .commands("money give " + PLAYER_NAME + " " + VAR_AMOUNT_FULL)
            .variable(VAR_AMOUNT_NAME, 500, 5)
            .build()
        );

        rewards.put(QUEST_CASH_HIGH, Reward.builder("$" + VAR_AMOUNT_FULL)
            .commands("money give " + PLAYER_NAME + " " + VAR_AMOUNT_FULL)
            .variable(VAR_AMOUNT_NAME, 1000, 7.5)
            .build()
        );

        // For battle pass
        rewards.put(BP_CASH_LOW, Reward.builder("$" + VAR_AMOUNT_FULL)
            .commands("money give " + PLAYER_NAME + " " + VAR_AMOUNT_FULL)
            .variable(VAR_AMOUNT_NAME, 300, 0)
            .build()
        );

        rewards.put(BP_CASH_MEDIUM, Reward.builder("$" + VAR_AMOUNT_FULL)
            .commands("money give " + PLAYER_NAME + " " + VAR_AMOUNT_FULL)
            .variable(VAR_AMOUNT_NAME, 750, 0)
            .build()
        );

        rewards.put(BP_CASH_HIGH, Reward.builder("$" + VAR_AMOUNT_FULL)
            .commands("money give " + PLAYER_NAME + " " + VAR_AMOUNT_FULL)
            .variable(VAR_AMOUNT_NAME, 1500, 0)
            .build()
        );

        rewards.put(BP_GOLD_INGOTS, Reward.builder(YELLOW.wrap("8x Gold Ingots"))
            .commands("give " + PLAYER_NAME + " gold_ingot 8")
            .build()
        );

        rewards.put(BP_DIAMONDS, Reward.builder(AQUA.wrap("3x Diamonds"))
            .commands("give " + PLAYER_NAME + " diamond 3")
            .build()
        );

        return rewards;
    }
}
