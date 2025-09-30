package su.nightexpress.quests.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.EnumLocale;
import su.nightexpress.nightcore.locale.entry.IconLocale;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.locale.message.MessageData;
import su.nightexpress.quests.battlepass.definition.BattlePassType;
import su.nightexpress.quests.quest.command.QuestsCommands;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.quests.QuestsPlaceholders.*;

public class Lang implements LangContainer {

    public static final EnumLocale<BattlePassType> BATTLE_PASS_MODE = LangEntry.builder("BattlePassMode").enumeration(BattlePassType.class);

    public static final TextLocale COMMAND_ARGUMENT_NAME_MILESTONE = LangEntry.builder("Command.ArgumentName.Milestone").text("milestone");
    public static final TextLocale COMMAND_ARGUMENT_NAME_QUEST     = LangEntry.builder("Command.ArgumentName.Quest").text("quest");
    public static final TextLocale COMMAND_ARGUMENT_NAME_DURATION  = LangEntry.builder("Command.ArgumentName.Duration").text("duration");

    public static final TextLocale COMMAND_BATTLE_PASS_NAME        = LangEntry.builder("Command.BattlePass.Name").text("Battle Pass");
    public static final TextLocale COMMAND_BATTLE_PASS_DESC        = LangEntry.builder("Command.BattlePass.Desc").text("View Battle Pass.");
    public static final TextLocale COMMAND_BATTLE_PASS_START_DESC  = LangEntry.builder("Command.BattlePass.Start.Desc").text("Start a new season.");
    public static final TextLocale COMMAND_BATTLE_PASS_CANCEL_DESC = LangEntry.builder("Command.BattlePass.Start.Desc").text("Cancel current or scheduled season.");

    public static final TextLocale COMMAND_BATTLE_PASS_ADD_LEVEL_DESC     = LangEntry.builder("Command.BattlePass.AddLevel.Desc").text("Add BattlePass levels.");
    public static final TextLocale COMMAND_BATTLE_PASS_REMOVE_LEVEL_DESC  = LangEntry.builder("Command.BattlePass.RemoveLevel.Desc").text("Remove BattlePass levels.");
    public static final TextLocale COMMAND_BATTLE_PASS_SET_LEVEL_DESC     = LangEntry.builder("Command.BattlePass.SetLevel.Desc").text("Set BattlePass levels.");
    public static final TextLocale COMMAND_BATTLE_PASS_ADD_XP_DESC        = LangEntry.builder("Command.BattlePass.AddXP.Desc").text("Add BattlePass XP.");
    public static final TextLocale COMMAND_BATTLE_PASS_REMOVE_XP_DESC     = LangEntry.builder("Command.BattlePass.RemoveXP.Desc").text("Remove BattlePass XP.");
    public static final TextLocale COMMAND_BATTLE_PASS_SET_XP_DESC        = LangEntry.builder("Command.BattlePass.SetXP.Desc").text("Set BattlePass XP.");
    
    public static final TextLocale COMMAND_QUESTS_NAME         = LangEntry.builder("Command.Quests.Name").text("Quests");
    public static final TextLocale COMMAND_QUESTS_DESC         = LangEntry.builder("Command.Quests.Desc").text("View daily quests.");
    public static final TextLocale COMMAND_QUESTS_REFRESH_DESC = LangEntry.builder("Command.Quests.Refresh.Desc").text("Refresh player's quests.");

    public static final TextLocale COMMAND_MILESTONES_NAME       = LangEntry.builder("Command.Milestones.Name").text("Milestones");
    public static final TextLocale COMMAND_MILESTONES_DESC       = LangEntry.builder("Command.Milestones.Desc").text("View milestones.");
    public static final TextLocale COMMAND_MILESTONES_RESET_DESC = LangEntry.builder("Command.Milestones.Reset.Desc").text("Reset a player's milestone.");

    public static final MessageLocale COMMAND_SYNTAX_INVALID_MILESTONE = LangEntry.builder("Command.Syntax.InvalidMilestone").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid milestone!")
    );

    public static final MessageLocale COMMAND_SYNTAX_INVALID_QUEST = LangEntry.builder("Command.Syntax.InvalidQuest").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid quest!")
    );

    public static final MessageLocale BATTLE_PASS_SEASON_CANCEL_NOTHING = LangEntry.builder("BattlePass.Season.Cancel.Nothing").chatMessage(
        GRAY.wrap("There is no active or scheduled season to cancel.")
    );

    public static final MessageLocale BATTLE_PASS_SEASON_CANCELLED = LangEntry.builder("BattlePass.Season.Cancelled").chatMessage(
        GRAY.wrap("Successfully cancelled the " + YELLOW.wrap(SEASON_NAME) + " season.")
    );

    public static final MessageLocale BATTLE_PASS_SEASON_SCHEDULE_ALREADY = LangEntry.builder("BattlePass.Season.Schedule.Already").chatMessage(
        GRAY.wrap("There is already an active or scheduled season. You need to cancel it first.")
    );

    /*public static final MessageLocale BATTLE_PASS_SEASON_SCHEDULED = LangEntry.builder("BattlePass.Season.Scheduled").message(
        MessageData.CHAT_NO_PREFIX,
        " ",
        YELLOW.and(BOLD).wrap("NEW SEASON IS COMING!"),
        DARK_GRAY.wrap("»") + " " + GRAY.wrap("Season: ") + YELLOW.wrap(SEASON_NAME),
        DARK_GRAY.wrap("»") + " " + GRAY.wrap("Launch Date: ") + YELLOW.wrap(SEASON_START_DATE),
        DARK_GRAY.wrap("»") + " " + GRAY.wrap("End Date: ") + YELLOW.wrap(SEASON_END_DATE),
        " "
    );*/

    public static final MessageLocale BATTLE_PASS_SEASON_LAUNCHED = LangEntry.builder("BattlePass.Season.Launched").message(
        MessageData.CHAT_NO_PREFIX,
        " ",
        GREEN.and(BOLD).wrap("SEASON " + WHITE.wrap(SEASON_NAME) + " JUST LAUNCHED!"),
        DARK_GRAY.wrap("»") + " " + GRAY.wrap("Duration: ") + GREEN.wrap(SEASON_TIME_LEFT),
        DARK_GRAY.wrap("»") + " " + GRAY.wrap("End Date: ") + GREEN.wrap(SEASON_END_DATE),
        //" ",
        //GRAY.wrap("Click " + RUN_COMMAND.with("/" + QuestsCommands.DEFAULT_ALIAS).wrap(YELLOW.and(BOLD).wrap("[HERE]")) + " to view your quests!"),
        " "
    );

    public static final MessageLocale BATTLE_PASS_SEASON_FINISHED = LangEntry.builder("BattlePass.Season.Finished").message(
        MessageData.CHAT_NO_PREFIX,
        " ",
        RED.and(BOLD).wrap("SEASON " + WHITE.wrap(SEASON_NAME) + " IS OVER!"),
        DARK_GRAY.wrap("»") + " " + GRAY.wrap("Launch Date: ") + RED.wrap(SEASON_START_DATE),
        DARK_GRAY.wrap("»") + " " + GRAY.wrap("End Date: ") + RED.wrap(SEASON_END_DATE),
        //" ",
        //GRAY.wrap("Click " + RUN_COMMAND.with("/" + BattlePassCommands.DEFAULT_ALIAS).wrap(YELLOW.and(BOLD).wrap("[HERE]")) + " to claim your rewards!"),
        " "
    );

    public static final MessageLocale BATTLE_PASS_LEVEL_ADDED = LangEntry.builder("BattlePass.Level.Added").chatMessage(
        GRAY.wrap("Added " + SOFT_GREEN.wrap(GENERIC_AMOUNT) + " level(s) to " + SOFT_GREEN.wrap(PLAYER_NAME) + "'s Battle Pass.")
    );

    public static final MessageLocale BATTLE_PASS_LEVEL_REMOVED = LangEntry.builder("BattlePass.Level.Removed").chatMessage(
        GRAY.wrap("Removed " + SOFT_RED.wrap(GENERIC_AMOUNT) + " level(s) from " + SOFT_RED.wrap(PLAYER_NAME) + "'s Battle Pass.")
    );

    public static final MessageLocale BATTLE_PASS_LEVEL_SET = LangEntry.builder("BattlePass.Level.Set").chatMessage(
        GRAY.wrap("Set " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " level for " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s Battle Pass.")
    );

    public static final MessageLocale BATTLE_PASS_XP_ADDED = LangEntry.builder("BattlePass.XP.Added").chatMessage(
        GRAY.wrap("Added " + SOFT_GREEN.wrap(GENERIC_AMOUNT) + " XP to " + SOFT_GREEN.wrap(PLAYER_NAME) + "'s Battle Pass.")
    );

    public static final MessageLocale BATTLE_PASS_XP_REMOVED = LangEntry.builder("BattlePass.XP.Removed").chatMessage(
        GRAY.wrap("Removed " + SOFT_RED.wrap(GENERIC_AMOUNT) + " XP from " + SOFT_RED.wrap(PLAYER_NAME) + "'s Battle Pass.")
    );

    public static final MessageLocale BATTLE_PASS_XP_SET = LangEntry.builder("BattlePass.XP.Set").chatMessage(
        GRAY.wrap("Set " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " XP for " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s Battle Pass.")
    );

    public static final MessageLocale BATTLE_PASS_LEVEL_UP = LangEntry.builder("BattlePass.Level.Up").titleMessage(
        GREEN.wrap("↑ " + BOLD.and(UNDERLINED).wrap("LEVEL UP") + " ↑"),
        GRAY.wrap("Your Battle Pass level raised to " + GREEN.wrap(BATTLE_PASS_LEVEL) + "!"),
        Sound.ENTITY_PLAYER_LEVELUP
    );

    public static final MessageLocale BATTLE_PASS_LEVEL_DOWN = LangEntry.builder("BattlePass.Level.Downgrade").titleMessage(
        RED.wrap("↓ " + BOLD.and(UNDERLINED).wrap("LEVEL DOWNGRADE") + " ↓"),
        GRAY.wrap("Your Battle Pass level decreased to " + RED.wrap(BATTLE_PASS_LEVEL) + "!"),
        Sound.ENTITY_IRON_GOLEM_DEATH
    );

    /*public static final MessageLocale BATTLE_PASS_LEVEL_REWARDS_NOTIFY = LangEntry.builder("BattlePass.LevelRewards.Notify").message(MessageData.CHAT_NO_PREFIX,
        " ",
        GRAY.wrap(SOFT_YELLOW.and(BOLD).wrap("REWARDS:") + " You have " + SOFT_YELLOW.and(BOLD.and(UNDERLINED)).wrap(GENERIC_AMOUNT) + " Battle Pass rewards available! " +
            RUN_COMMAND.with("/" + CharacterCommands.DEFAULT_ALIAS + " " + CharacterCommands.REWARDS_ALIAS).wrap(SHOW_TEXT.with(GRAY.wrap("Click to view your rewards!")).wrap(SOFT_YELLOW.wrap("[Click to Claim]")))),
        " "
    );

    public static final MessageLocale BATTLE_PASS_LEVEL_REWARDS_RECEIVE = LangEntry.builder("BattlePass.LevelRewards.Receive").message(MessageData.CHAT_NO_PREFIX,
        " ",
        GRAY.wrap(SOFT_YELLOW.and(BOLD).wrap("REWARDS:") + " You received " + SOFT_YELLOW.and(BOLD.and(UNDERLINED)).wrap(GENERIC_AMOUNT) + " character rewards! " +
            SHOW_TEXT.with(GENERIC_REWARDS).wrap(SOFT_YELLOW.wrap("[Hover to View]"))),
        " "
    );

    public static final TextLocale REWARDS_ENTRY = LangEntry.builder("Other.Rewards.Entry").text(
        GRAY.wrap(REWARD_NAME)
    );*/

    public static final MessageLocale BATTLE_PASS_NO_ACTIVE_SEASON = LangEntry.builder("BattlePass.NoActiveSeason").chatMessage(
        GRAY.wrap("There is no active season.")
    );

    public static final MessageLocale QUESTS_LOCKED = LangEntry.builder("Quests.Locked").chatMessage(
        GRAY.wrap("Quests are not available currently.")
    );
    
    public static final MessageLocale QUESTS_REFRESHED_FOR = LangEntry.builder("Quests.RefreshedFor").chatMessage(
        GRAY.wrap("Refreshed quests for " + YELLOW.wrap(PLAYER_NAME) + ".")
    );

    public static final MessageLocale QUESTS_REFRESHED = LangEntry.builder("Quests.Refreshed")
        .message(MessageData.CHAT_NO_PREFIX,
            " ",
            YELLOW.and(BOLD).wrap("NEW QUESTS AVAILABLE!"),
            DARK_GRAY.wrap("»") + " " + GRAY.wrap("You have " + YELLOW.wrap(GENERIC_AMOUNT) + " new daily quests available."),
            DARK_GRAY.wrap("»") + " " + GRAY.wrap("Click " + RUN_COMMAND.with("/" + QuestsCommands.DEFAULT_ALIAS).wrap(YELLOW.and(BOLD).wrap("HERE")) + " to view them!"),
            " "
        );

    public static final MessageLocale QUESTS_QUEST_TIME_OUT = LangEntry.builder("Quests.Quest.TimeOut")
        .message(MessageData.CHAT_NO_PREFIX,
            " ",
            SOFT_RED.and(BOLD).wrap("QUEST FAILED!"),
            DARK_GRAY.wrap("»") + " " + GRAY.wrap("You failed to complete the " + SOFT_RED.wrap(QUEST_NAME) + " quest in " + SOFT_RED.wrap(QUEST_COMPLETION_TIME) + "!"),
            " "
        );

    public static final MessageLocale QUESTS_QUEST_ACCEPTED = LangEntry.builder("Quests.Quest.Accepted")
        .message(MessageData.CHAT_NO_PREFIX,
            " ",
            YELLOW.and(BOLD).wrap("QUEST ACCEPTED!"),
            DARK_GRAY.wrap("»") + " " + GRAY.wrap("Complete the " + YELLOW.wrap(QUEST_NAME) + " quest in " + YELLOW.wrap(QUEST_COMPLETION_TIME) + " to get rewards!"),
            " "
        );

    public static final MessageLocale QUESTS_QUEST_COMPLETED_XP_ONLY = LangEntry.builder("Quests.Quest.Completed.XPOnly")
        .message(MessageData.CHAT_NO_PREFIX,
            " ",
            GREEN.and(BOLD).wrap("QUEST COMPLETED!"),
            DARK_GRAY.wrap("»") + " " + GRAY.wrap("You completed the " + GREEN.wrap(QUEST_NAME) + " quest!"),
            DARK_GRAY.wrap("»") + " " + GRAY.wrap("Battle Pass XP: " + GREEN.wrap(GENERIC_XP)),
            " "
        );

    public static final MessageLocale QUESTS_QUEST_COMPLETED_XP_REWARDS = LangEntry.builder("Quests.Quest.Completed.XPRewards")
        .message(MessageData.CHAT_NO_PREFIX,
            " ",
            GREEN.and(BOLD).wrap("QUEST COMPLETED!"),
            DARK_GRAY.wrap("»") + " " + GRAY.wrap("You completed the " + GREEN.wrap(QUEST_NAME) + " quest!"),
            DARK_GRAY.wrap("»") + " " + GRAY.wrap("Battle Pass XP: " + GREEN.wrap(GENERIC_XP)),
            DARK_GRAY.wrap("»") + " " + GRAY.wrap("Reward(s): " + GREEN.wrap(GENERIC_REWARDS)),
            " "
        );

    public static final MessageLocale MILESTONES_RESET_FOR = LangEntry.builder("Milestones.ResetFor").chatMessage(
        GRAY.wrap("Successfully reset " + YELLOW.wrap(MILESTONE_NAME) + " milestone progress for " + YELLOW.wrap(PLAYER_NAME) + "!")
    );

    public static final MessageLocale MILESTONES_MILESTONE_COMPLETED = LangEntry.builder("Milestones.Milestone.Completed")
        .message(MessageData.CHAT_NO_PREFIX,
            " ",
            GREEN.and(BOLD).wrap("MILESTONE COMPLETED!"),
            DARK_GRAY.wrap("»") + " " + GRAY.wrap("You completed " + WHITE.wrap("Level " + GENERIC_LEVEL) + " of the " + GREEN.wrap(MILESTONE_NAME) + " milestone!"),
            DARK_GRAY.wrap("»") + " " + GRAY.wrap("Reward(s): " + GREEN.wrap(GENERIC_REWARDS)),
            " "
        );

    public static final IconLocale UI_MILESTONES_CATEGORY_INFO = LangEntry.iconBuilder("UI.Milestones.CategoryInfo")
        .rawName(YELLOW.wrap(MILESTONE_CATEGORY_NAME) + DARK_GRAY.wrap(" ┃ ") + GOLD.wrap("Milestone"))
        .rawLore(
            MILESTONE_CATEGORY_DESCRIPTION,
            EMPTY_IF_ABOVE,
            DARK_GRAY.wrap("•") + WHITE.wrap(" Total Milestones: ") + YELLOW.wrap(GENERIC_TOTAL),
            DARK_GRAY.wrap("•") + WHITE.wrap(" Levels Completed: ") + YELLOW.wrap(GENERIC_LEVELS),
            DARK_GRAY.wrap("•") + WHITE.wrap(" Milestones Completed: ") + YELLOW.wrap(GENERIC_COMPLETED),
            "",
            YELLOW.wrap("→ " + UNDERLINED.wrap("Click to view!"))
        )
        .build();

    public static final IconLocale UI_MILESTONES_MILESTONE_INFO = LangEntry.iconBuilder("UI.Milestones.MilestoneInfo")
        .rawName(GOLD.wrap(MILESTONE_NAME))
        .rawLore(
            MILESTONE_DESCRIPTION,
            EMPTY_IF_ABOVE,
            GOLD.wrap("Progress:"),
            DARK_GRAY.wrap("»") + " " + GRAY.wrap("Level: " + WHITE.wrap(GENERIC_LEVEL) + "/" + WHITE.wrap(MILESTONE_LEVELS)),
            DARK_GRAY.wrap("»" + " " + GENERIC_PROGRESS_BAR + " (" + WHITE.wrap(GENERIC_PROGRESS + "%") + ")"),
            GENERIC_OBJECTIVES,
            "",
            GOLD.wrap("Rewards:"),
            GENERIC_REWARDS,
            "",
            GOLD.wrap("→ " + UNDERLINED.wrap("Click for details!"))
        )
        .build();

    public static final IconLocale UI_QUEST_AVAILABLE = LangEntry.iconBuilder("UI.Quest.Available")
        .rawName(GOLD.wrap(QUEST_NAME) + GRAY.wrap(" • ") + WHITE.wrap("Available"))
        .rawLore(
            QUEST_DESCRIPTION,
            EMPTY_IF_ABOVE,
            GOLD.wrap("⏳ Completion Time:") + " " + WHITE.wrap(QUEST_COMPLETION_TIME),
            "",
            GOLD.wrap("Objectives:"),
            GENERIC_OBJECTIVES,
            "",
            GOLD.wrap("Rewards:"),
            GENERIC_BATTLE_PASS_REWARDS,
            GENERIC_REWARDS,
            "",
            GOLD.wrap("→ " + UNDERLINED.wrap("Click to accept!"))
        )
        .build();

    public static final IconLocale UI_QUEST_IN_PROGRESS = LangEntry.iconBuilder("UI.Quest.InProgress")
        .rawName(GOLD.wrap(QUEST_NAME) + GRAY.wrap(" • ") + YELLOW.wrap("In Progress"))
        .rawLore(
            QUEST_DESCRIPTION,
            EMPTY_IF_ABOVE,
            RED.wrap("⏳ Timeleft:") + " " + WHITE.wrap(GENERIC_TIMELEFT),
            "",
            GOLD.wrap("Progress:"),
            DARK_GRAY.wrap("»" + " " + GENERIC_PROGRESS_BAR + " (" + WHITE.wrap(GENERIC_PROGRESS + "%") + ")"),
            GENERIC_OBJECTIVES,
            "",
            GOLD.wrap("Rewards:"),
            GENERIC_BATTLE_PASS_REWARDS,
            GENERIC_REWARDS
        )
        .build();

    public static final IconLocale UI_QUEST_COMPLETED = LangEntry.iconBuilder("UI.Quest.Completed")
        .rawName(GOLD.wrap(QUEST_NAME) + GRAY.wrap(" • ") + GREEN.wrap("Completed"))
        .rawLore(
            QUEST_DESCRIPTION,
            EMPTY_IF_ABOVE,
            GOLD.wrap("Progress:"),
            DARK_GRAY.wrap("»" + " " + GENERIC_PROGRESS_BAR + " (" + WHITE.wrap(GENERIC_PROGRESS + "%") + ")"),
            GENERIC_OBJECTIVES,
            "",
            GOLD.wrap("Rewards:"),
            GENERIC_BATTLE_PASS_REWARDS,
            GENERIC_REWARDS
        )
        .build();

    public static final IconLocale UI_QUEST_FAILED = LangEntry.iconBuilder("UI.Quest.Failed")
        .rawName(GOLD.wrap(QUEST_NAME) + GRAY.wrap(" • ") + RED.wrap("Failed"))
        .rawLore(
            QUEST_DESCRIPTION,
            EMPTY_IF_ABOVE,
            GOLD.wrap("Progress:"),
            DARK_GRAY.wrap("»" + " " + GENERIC_PROGRESS_BAR + " (" + WHITE.wrap(GENERIC_PROGRESS + "%") + ")"),
            GENERIC_OBJECTIVES,
            "",
            GOLD.wrap("Rewards:"),
            GENERIC_BATTLE_PASS_REWARDS,
            GENERIC_REWARDS
        )
        .build();

    public static final TextLocale UI_MILESTONES_MILESTONE_OBJECTIVE = LangEntry.builder("UI.Milestones.MilestoneObjective")
        .text(DARK_GRAY.wrap("» " + WHITE.wrap(GENERIC_NAME + ":") + " " + SOFT_YELLOW.wrap(GENERIC_CURRENT) + "/" + SOFT_YELLOW.wrap(GENERIC_REQUIRED)));

    public static final TextLocale UI_ENTRY_REWARD = LangEntry.builder("UI.Entry.Reward.Custom")
        .text(DARK_GRAY.wrap("┃ " + WHITE.wrap(GENERIC_NAME)));

    public static final TextLocale UI_ENTRY_REWARD_BATTLE_PASS_XP = LangEntry.builder("UI.Entry.Reward.BattlePass.XP")
        .text(DARK_GRAY.wrap("┃ " + WHITE.wrap("Battle Pass XP: ") + YELLOW.wrap(GENERIC_XP)));

}
