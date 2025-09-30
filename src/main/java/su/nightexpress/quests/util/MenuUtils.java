package su.nightexpress.quests.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.text.night.NightMessage;
import su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.config.Lang;
import su.nightexpress.quests.quest.definition.QuestXPReward;
import su.nightexpress.quests.reward.Reward;
import su.nightexpress.quests.task.adapter.Adapter;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.milestone.data.MilestoneData;
import su.nightexpress.quests.quest.data.QuestData;
import su.nightexpress.quests.milestone.definition.Milestone;
import su.nightexpress.quests.quest.definition.Quest;
import su.nightexpress.quests.milestone.definition.MilestoneObjective;

import java.util.*;

import static su.nightexpress.quests.QuestsPlaceholders.*;

public class MenuUtils {

    @NotNull
    public static String buildProgressBar(double percent) {
        int length = Config.UI_PROGRESS_BAR_LENGTH.get();
        int filled = Math.clamp((int) Math.ceil(length * percent), 0, length);

        String colorFill = Config.UI_PROGRESS_BAR_COLOR_FILL.get();
        String colorEmpty = Config.UI_PROGRESS_BAR_COLOR_EMPTY.get();
        String point = Config.UI_PROGRESS_BAR_CHAR.get();

        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < length; index++) {
            String color = filled > index ? colorFill : colorEmpty;
            builder.append(TagWrappers.COLOR.with(color).wrap(point));
        }
        return builder.toString();
    }

    @NotNull
    public static List<String> formatObjectives(@NotNull Milestone milestone, @NotNull MilestoneData data, int level) {
        List<String> list = new ArrayList<>();
        TaskType<?, ?> type = milestone.getType();

        milestone.getObjectiveTable().getEntryMap().entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            String fullName = entry.getKey();
            MilestoneObjective objective = entry.getValue();

            int required = objective.getAmount(level);
            int current = data.isLevelCompleted(level) ? required : data.getObjectiveProgress(fullName);

            list.add(formatObjective(type, fullName, current, required));
        });

        return list;
    }

    @NotNull
    public static List<String> formatObjectives(@NotNull Quest quest, @NotNull QuestData data) {
        List<String> list = new ArrayList<>();
        TaskType<?, ?> type = quest.getType();

        data.getObjectiveCounterMap().forEach((fullName, counter) -> {
            int required = counter.getRequired();
            int current = counter.getCompleted();

            list.add(formatObjective(type, fullName, current, required));
        });

        /*quest.getObjectiveTable().getEntryMap().entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            String fullName = entry.getKey();

            int required = data.getRequired(fullName);
            int current = data.getCurrent(fullName);

            list.add(formatObjective(type, fullName, current, required));
        });*/

        return list;
    }

    @NotNull
    private static String formatObjective(@NotNull TaskType<?, ?> type, @NotNull String fullName, int current, int required) {
        Adapter<?, ?> adapter = type.getAdapterFamily().getAdapterForName(fullName);
        String name = adapter == null ? fullName : adapter.getLocalizedName(fullName);

        return Lang.UI_MILESTONES_MILESTONE_OBJECTIVE.text()
            .replace(GENERIC_NAME, String.valueOf(name))
            .replace(GENERIC_CURRENT, NumberUtil.format(current))
            .replace(GENERIC_REQUIRED, NumberUtil.format(required));
    }

    @NotNull
    public static List<String> formatRewards(@NotNull List<Reward> rewards, int units, int level, double scale) {
        return rewards.stream()
            .sorted(Comparator.comparing(reward -> NightMessage.stripTags(reward.getName(units, level, scale))))
            .map(reward -> Lang.UI_ENTRY_REWARD.text().replace(GENERIC_NAME, reward.getName(units, level, scale)))
            .toList();
    }

    @NotNull
    public static List<String> formatBattlePassRewards(@NotNull QuestXPReward reward, double unitsWorth) {
        return Lists.newList(
            Lang.UI_ENTRY_REWARD_BATTLE_PASS_XP.text().replace(GENERIC_XP, NumberUtil.format(reward.getXP(unitsWorth)))
        );
    }
}
