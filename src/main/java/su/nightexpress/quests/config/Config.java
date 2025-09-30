package su.nightexpress.quests.config;

import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.RankTable;
import su.nightexpress.quests.battlepass.command.BattlePassCommands;
import su.nightexpress.quests.milestone.command.MilestoneCommands;
import su.nightexpress.quests.quest.command.QuestsCommands;
import su.nightexpress.quests.util.QuestUtils;

import java.util.Set;

import static org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class Config {

    public static final String DIR_QUESTS     = "/quests/";
    public static final String DIR_MILESTONES = "/milestones/";
    public static final String DIR_MENU       = "/menu/";

    public static final ConfigValue<String> GENERAL_DATE_TIME_FORMAT = ConfigValue.create("General.DateTimeFormat",
        "dd/MM/yyyy HH:mm",
        "Sets date time format."
    ).whenRead(QuestUtils::setDateTimeFormatter);

    public static final ConfigValue<Boolean> FEATURES_BATTLE_PASS_ENABLED = ConfigValue.create("Features.BattlePass.Enabled",
        true,
        "Enables the Battle Pass feature."
    );

    public static final ConfigValue<String[]> FEATURES_BATTLE_PASS_ALIASES = ConfigValue.create("Features.BattlePass.Aliases",
        new String[]{BattlePassCommands.DEFAULT_ALIAS, "bp"},
        "Command aliases for the Battle Pass feature.",
        "[*] Server reboot is highly recommended when changed."
    );

    public static final ConfigValue<Boolean> FEATURES_QUESTS_ENABLED = ConfigValue.create("Features.Quests.Enabled",
        true,
        "Enables the Daily Quests feature."
    );

    public static final ConfigValue<String[]> FEATURES_QUESTS_ALIASES = ConfigValue.create("Features.Quests.Aliases",
        new String[]{QuestsCommands.DEFAULT_ALIAS},
        "Command aliases for the Daily Quests feature.",
        "[*] Server reboot is highly recommended when changed."
    );

    public static final ConfigValue<Boolean> FEATURES_MILESTONES_ENABLED = ConfigValue.create("Features.Milestones.Enabled",
        true,
        "Enables the milestones feature."
    );

    public static final ConfigValue<String[]> FEATURES_MILESTONES_ALIASES = ConfigValue.create("Features.Milestones.Aliases",
        new String[]{MilestoneCommands.DEFAULT_ALIAS},
        "Command aliases for the Milestones feature.",
        "[*] Server reboot is highly recommended when changed."
    );

    public static final ConfigValue<Set<String>> INTERGRATIONS_DISABLED = ConfigValue.create("Integrations.Disabled",
        Lists.newSet("PluginName", "AnotherPlugin"),
        "List here plugin names that that you want to disable integrations for."
    );

    public static final ConfigValue<Boolean> MILESTONES_RESET_PROGRESS = ConfigValue.create("Milestones.ResetProgress",
        false,
        "Controls whether milestone's progress will be reset for every next level."
    );

    public static final ConfigValue<Boolean> QUESTS_BATTLE_PASS_MODE = ConfigValue.create("Quests.BattlePassMode",
        false,
        "When set on 'true', daily quests are only available during the Battle Pass season."
    );

    public static final ConfigValue<RankTable> QUESTS_AMOUT_PER_RANK = ConfigValue.create("Quests.AmountPerRank",
        RankTable::read,
        RankTable.builder(RankTable.Mode.RANK, 3).addRankValue("vip", 4).addRankValue("premium", 5).build(),
        "Amount of randomly generated daily quests for players based on their rank/permissions."
    );

    public static final ConfigValue<Boolean> ANTI_ABUSE_COUNT_PLAYER_BLOCKS = ConfigValue.create("AntiAbuse.CountPlayerBlocks",
        false,
        "Whether to count blocks placed by players for block related quests and milestones."
    );

    public static final ConfigValue<Boolean> ANTI_ABUSE_COUNT_ARTIFICAL_MOBS = ConfigValue.create("AntiAbuse.CountArtificallySpawnedMobs",
        false,
        "Whether to count mobs spawned artifically for mob related quests and milestones."
    );

    public static final ConfigValue<Set<String>> ANTI_ABUSE_ARTIFICAL_MOB_SPAWNS = ConfigValue.create("AntiAbuse.ArtificalMobSpawns",
        Lists.newSet(
            SpawnReason.EGG.name(),
            SpawnReason.SPAWNER.name(),
            SpawnReason.SPAWNER_EGG.name(),
            SpawnReason.DISPENSE_EGG.name(),
            SpawnReason.TRIAL_SPAWNER.name(),
            SpawnReason.BUILD_SNOWMAN.name(),
            SpawnReason.BUILD_IRONGOLEM.name()
        ),
        "List of spawn reasons considered artifical.",
        "https://jd.papermc.io/paper/1.21.8/org/bukkit/event/entity/CreatureSpawnEvent.SpawnReason.html"
    );

    public static final ConfigValue<Boolean> ANTI_ABUSE_COUNT_IN_VEHICLES = ConfigValue.create("AntiAbuse.CountInVehicles",
        true,
        "Whether to count quests/milestones progress for players in vehicles (minecarts, boats, etc.)."
    );

    public static final ConfigValue<Boolean> ANTI_ABUSE_COUNT_AUTO_COOKING = ConfigValue.create("AntiAbuse.CountAutoCooking",
        true,
        "Whether to count quests/milestones progress for automated brewing, cooking and smelting."
    );

    public static final ConfigValue<Integer> UI_PROGRESS_BAR_LENGTH = ConfigValue.create("UI.ProgressBar.Length",
        15
    );

    public static final ConfigValue<String> UI_PROGRESS_BAR_CHAR = ConfigValue.create("UI.ProgressBar.Char",
        "■"
    );

    public static final ConfigValue<String> UI_PROGRESS_BAR_COLOR_FILL = ConfigValue.create("UI.ProgressBar.FillColor",
        "#32E632"
    );

    public static final ConfigValue<String> UI_PROGRESS_BAR_COLOR_EMPTY = ConfigValue.create("UI.ProgressBar.EmptyColor",
        "#464646"
    );

    public static boolean isMilestonesResetProgress() {
        return MILESTONES_RESET_PROGRESS.get();
    }

    public static boolean isBattlePassEnabled() {
        return FEATURES_BATTLE_PASS_ENABLED.get();
    }

    public static boolean isQuestsEnabled() {
        return FEATURES_QUESTS_ENABLED.get();
    }

    public static boolean isQuestsForBattlePass() {
        return QUESTS_BATTLE_PASS_MODE.get();
    }
}
