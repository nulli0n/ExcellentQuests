package su.nightexpress.quests;

import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Placeholders;
import su.nightexpress.nightcore.util.placeholder.PlaceholderList;
import su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.quests.battlepass.data.BattlePassData;
import su.nightexpress.quests.battlepass.definition.BattlePassSeason;
import su.nightexpress.quests.milestone.definition.Milestone;
import su.nightexpress.quests.milestone.definition.MilestoneCategory;
import su.nightexpress.quests.quest.definition.Quest;
import su.nightexpress.quests.util.QuestUtils;

import java.util.function.Function;

public class QuestsPlaceholders extends Placeholders {

    public static final String GENERIC_TOTAL               = "%total%";
    public static final String GENERIC_COMPLETED           = "%completed%";
    public static final String GENERIC_LEVELS              = "%levels%";
    public static final String GENERIC_PERCENT             = "%percent%";
    public static final String GENERIC_PROGRESS_BAR        = "%progress_bar%";
    public static final String GENERIC_PROGRESS            = "%progress%";
    public static final String GENERIC_LEVEL               = "%level%";
    public static final String GENERIC_OBJECTIVES          = "%objectives%";
    public static final String GENERIC_REQUIRED            = "%required%";
    public static final String GENERIC_CURRENT             = "%currrent%";
    public static final String GENERIC_REWARDS             = "%rewards%";
    public static final String GENERIC_BATTLE_PASS_REWARDS = "%battle_pass_rewards%";
    public static final String GENERIC_TIMELEFT            = "%timeleft%";
    public static final String GENERIC_REFRESH_TIME        = "%refresh_time%";
    public static final String GENERIC_XP                  = "%xp%";

    public static final Function<String, String> VARIABLE = str -> "%" + str + "%";

    public static final String MILESTONE_CATEGORY_ID          = "%milestone_category_id%";
    public static final String MILESTONE_CATEGORY_NAME        = "%milestone_category_name%";
    public static final String MILESTONE_CATEGORY_DESCRIPTION = "%milestone_category_description%";

    public static final String MILESTONE_NAME        = "%milestone_name%";
    public static final String MILESTONE_DESCRIPTION = "%milestone_description%";
    public static final String MILESTONE_LEVELS      = "%milestone_levels%";

    public static final String QUEST_NAME            = "%quest_name%";
    public static final String QUEST_DESCRIPTION     = "%quest_description%";
    public static final String QUEST_COMPLETION_TIME = "%quest_completion_time%";

    public static final String BATTLE_PASS_LEVEL     = "%battle_pass_level%";
    public static final String BATTLE_PASS_XP        = "%battle_pass_xp%";
    public static final String BATTLE_PASS_XP_MAX    = "%battle_pass_xp_max%";
    public static final String BATTLE_PASS_XP_TO_UP  = "%battle_pass_xp_to_up%";
    public static final String BATTLE_PASS_MAX_LEVEL = "%battle_pass_max_level%";
    public static final String BATTLE_PASS_TYPE      = "%battle_pass_type%";

    public static final String SEASON_NAME        = "%season_name%";
    public static final String SEASON_START_DATE  = "%season_start_date%";
    public static final String SEASON_END_DATE    = "%season_end_date%";
    public static final String SEASON_EXPIRE_DATE = "%season_expire_date%";
    public static final String SEASON_DURATION    = "%season_duration%";
    public static final String SEASON_TIME_LEFT   = "%season_timeleft%";


    public static final PlaceholderList<MilestoneCategory> MILESTONE_CATEGORY = PlaceholderList.create(list -> list
        .add(MILESTONE_CATEGORY_ID, MilestoneCategory::getId)
        .add(MILESTONE_CATEGORY_NAME, MilestoneCategory::getName)
        .add(MILESTONE_CATEGORY_DESCRIPTION, category -> String.join(TagWrappers.BR, category.getDescription()))
    );

    public static final PlaceholderList<Milestone> MILESTONE = PlaceholderList.create(list -> list
        .add(MILESTONE_NAME, Milestone::getName)
        .add(MILESTONE_DESCRIPTION, milestone -> String.join(TagWrappers.BR, milestone.getDescription()))
        .add(MILESTONE_LEVELS, milestone -> String.valueOf(milestone.getLevels()))
    );

    public static final PlaceholderList<Quest> QUEST = PlaceholderList.create(list -> list
        .add(QUEST_NAME, Quest::getName)
        .add(QUEST_DESCRIPTION, quest -> String.join(TagWrappers.BR, quest.getDescription()))
        .add(QUEST_COMPLETION_TIME, quest -> TimeFormats.toLiteral(quest.getCompletionTime() * 1000L))
    );

    public static final PlaceholderList<BattlePassData> BATTLE_PASS_DATA = PlaceholderList.create(list -> list
        .add(BATTLE_PASS_MAX_LEVEL, data -> NumberUtil.format(data.getMaxLevel()))
        .add(BATTLE_PASS_LEVEL, data -> NumberUtil.format(data.getLevel()))
        .add(BATTLE_PASS_XP, data -> NumberUtil.format(data.getXP()))
        .add(BATTLE_PASS_XP_MAX, data -> NumberUtil.format(data.getLevelXP()))
        .add(BATTLE_PASS_XP_TO_UP, data -> NumberUtil.format(data.getXPToLevelUp()))
        //.add(BATTLE_PASS_TYPE, data -> Lang.BATTLE_PASS_MODE.getLocalized(data.isPremium() ? BattlePassType.PREMIUM : BattlePassType.FREE))
    );

    public static final PlaceholderList<BattlePassSeason> SEASON = PlaceholderList.create(list -> list
        .add(SEASON_NAME, BattlePassSeason::getName)
        .add(SEASON_START_DATE, season -> QuestUtils.formatDateTime(season.getStartDate()))
        .add(SEASON_END_DATE, season -> QuestUtils.formatDateTime(season.getEndDate()))
        .add(SEASON_EXPIRE_DATE, season -> QuestUtils.formatDateTime(season.getExpireDate()))
        .add(SEASON_DURATION, season -> TimeFormats.formatSince(season.getStartDate(), TimeFormatType.LITERAL))
        .add(SEASON_TIME_LEFT, season -> TimeFormats.formatDuration(season.getEndDate(), TimeFormatType.LITERAL))
    );
}
