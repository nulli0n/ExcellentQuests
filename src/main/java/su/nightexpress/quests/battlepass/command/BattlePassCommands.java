package su.nightexpress.quests.battlepass.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.command.NightCommand;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.quests.QuestsPlaceholders;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.battlepass.BattlePassManager;
import su.nightexpress.quests.battlepass.config.BattlePassConfig;
import su.nightexpress.quests.battlepass.data.BattlePassData;
import su.nightexpress.quests.battlepass.definition.BattlePassSeason;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.config.Lang;
import su.nightexpress.quests.config.Perms;
import su.nightexpress.quests.user.QuestUser;

public class BattlePassCommands {

    public static final String DEFAULT_ALIAS = "battlepass";

    private static final String ARG_PLAYER   = "player";
    private static final String ARG_NAME     = "name";
    private static final String ARG_DURATION = "duration";
    private static final String ARG_AMOUNT = "amount";

    private static QuestsPlugin      plugin;
    private static BattlePassManager manager;
    private static NightCommand      command;

    public static void load(@NotNull QuestsPlugin questsPlugin, @NotNull BattlePassManager passManager) {
        plugin = questsPlugin;
        manager = passManager;

        command = NightCommand.hub(plugin, Config.FEATURES_BATTLE_PASS_ALIASES.get(), builder -> builder
            .localized(Lang.COMMAND_BATTLE_PASS_NAME)
            .permission(Perms.COMMAND_BATTLE_PASS)
            .description(Lang.COMMAND_BATTLE_PASS_DESC)
            .branch(Commands.literal("start")
                .permission(Perms.COMMAND_BATTLE_PASS_START)
                .description(Lang.COMMAND_BATTLE_PASS_START_DESC)
                .withArguments(
                    Arguments.string(ARG_NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME).suggestions((reader, context) -> Lists.newList("<name>")),
                    Arguments.integer(ARG_DURATION, 1, 365).localized(Lang.COMMAND_ARGUMENT_NAME_DURATION).suggestions((reader, context) -> Lists.newList("30", "45", "60"))
                )
                .executes(BattlePassCommands::scheduleSeason)
            )
            .branch(Commands.literal("cancel")
                .permission(Perms.COMMAND_BATTLE_PASS_CANCEL)
                .description(Lang.COMMAND_BATTLE_PASS_CANCEL_DESC)
                .executes(BattlePassCommands::cancelSeason)
            )
            .branch(Commands.literal("addlevel")
                .description(Lang.COMMAND_BATTLE_PASS_ADD_LEVEL_DESC)
                .permission(Perms.COMMAND_BATTLE_PASS_ADD_LEVEL)
                .withArguments(
                    Arguments.integer(ARG_AMOUNT, 1, BattlePassConfig.getMaxLevel()).localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT).suggestions((reader, context) -> Lists.newList("1", "5", "10")),
                    Arguments.playerName(ARG_PLAYER).optional()
                )
                .executes(BattlePassCommands::addLevel)
            )
            .branch(Commands.literal("removelevel")
                .description(Lang.COMMAND_BATTLE_PASS_REMOVE_LEVEL_DESC)
                .permission(Perms.COMMAND_BATTLE_PASS_REMOVE_LEVEL)
                .withArguments(
                    Arguments.integer(ARG_AMOUNT, 1, BattlePassConfig.getMaxLevel()).localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT).suggestions((reader, context) -> Lists.newList("1", "5", "10")),
                    Arguments.playerName(ARG_PLAYER).optional()
                )
                .executes(BattlePassCommands::removeLevel)
            )
            .branch(Commands.literal("setlevel")
                .description(Lang.COMMAND_BATTLE_PASS_SET_LEVEL_DESC)
                .permission(Perms.COMMAND_BATTLE_PASS_SET_LEVEL)
                .withArguments(
                    Arguments.integer(ARG_AMOUNT, 0, BattlePassConfig.getMaxLevel()).localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT).suggestions((reader, context) -> Lists.newList("0", "1", "5", "10")),
                    Arguments.playerName(ARG_PLAYER).optional()
                )
                .executes(BattlePassCommands::setLevel)
            )
            .branch(Commands.literal("addxp")
                .description(Lang.COMMAND_BATTLE_PASS_ADD_XP_DESC)
                .permission(Perms.COMMAND_BATTLE_PASS_ADD_XP)
                .withArguments(
                    Arguments.integer(ARG_AMOUNT, 1).localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT).suggestions((reader, context) -> Lists.newList("10", "50", "100")),
                    Arguments.playerName(ARG_PLAYER).optional()
                )
                .executes(BattlePassCommands::addXP)
            )
            .branch(Commands.literal("removexp")
                .description(Lang.COMMAND_BATTLE_PASS_REMOVE_XP_DESC)
                .permission(Perms.COMMAND_BATTLE_PASS_REMOVE_XP)
                .withArguments(
                    Arguments.integer(ARG_AMOUNT, 1).localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT).suggestions((reader, context) -> Lists.newList("10", "50", "100")),
                    Arguments.playerName(ARG_PLAYER).optional()
                )
                .executes(BattlePassCommands::removeXP)
            )
            .branch(Commands.literal("setxp")
                .description(Lang.COMMAND_BATTLE_PASS_SET_XP_DESC)
                .permission(Perms.COMMAND_BATTLE_PASS_SET_XP)
                .withArguments(
                    Arguments.integer(ARG_AMOUNT, 0).localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT).suggestions((reader, context) -> Lists.newList("0", "10", "50", "100")),
                    Arguments.playerName(ARG_PLAYER).optional()
                )
                .executes(BattlePassCommands::setXP)
            )
            .executes(BattlePassCommands::openBattlePass)
        );
        command.register();
    }

    public static void shutdown() {
        command.unregister();
        command = null;
        manager = null;
        plugin = null;
    }

    private interface ProgressionOperation {

        void perform(@NotNull QuestUser user, int amount);
    }

    private static boolean openBattlePass(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        manager.openBattlePass(player);
        return true;
    }

    private static boolean scheduleSeason(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        CommandSender sender = context.getSender();
        String name = arguments.getString(ARG_NAME);
        int days = arguments.getInt(ARG_DURATION);

        manager.scheduleSeason(sender, name, days);
        return true;
    }

    private static boolean cancelSeason(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        manager.cancelSeason(context.getSender());
        return true;
    }

    public static boolean addLevel(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return peroformProgressionOperation(context, arguments, manager::handleLevelAdd, Lang.BATTLE_PASS_LEVEL_ADDED);
    }

    public static boolean removeLevel(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return peroformProgressionOperation(context, arguments, manager::handleLevelRemove, Lang.BATTLE_PASS_LEVEL_REMOVED);
    }

    public static boolean setLevel(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return peroformProgressionOperation(context, arguments, manager::handleLevelSet, Lang.BATTLE_PASS_LEVEL_SET);
    }

    public static boolean addXP(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return peroformProgressionOperation(context, arguments, manager::handleXPAdd, Lang.BATTLE_PASS_XP_ADDED);
    }

    public static boolean removeXP(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return peroformProgressionOperation(context, arguments, manager::handleXPRemove, Lang.BATTLE_PASS_XP_REMOVED);
    }

    public static boolean setXP(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return peroformProgressionOperation(context, arguments, manager::handleXPSet, Lang.BATTLE_PASS_XP_SET);
    }

    private static boolean peroformProgressionOperation(@NotNull CommandContext context,
                                                        @NotNull ParsedArguments arguments,
                                                        @NotNull ProgressionOperation operation,
                                                        @NotNull MessageLocale locale) {
        if (!arguments.contains(ARG_PLAYER) && !context.isPlayer()) {
            context.printUsage();
            return false;
        }

        BattlePassSeason season = manager.getSeason();
        if (season == null || (!season.isRunning() && !season.isScheduled())) {
            context.send(Lang.BATTLE_PASS_NO_ACTIVE_SEASON);
            return false;
        }

        int amount = arguments.getInt(ARG_AMOUNT);
        String playerName = arguments.getString(ARG_PLAYER, context.getSender().getName());

        plugin.getUserManager().manageUser(playerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            
            BattlePassData data = user.getBattlePassData(season);
            operation.perform(user, amount);
            context.send(locale, replacer -> replacer
                .replace(QuestsPlaceholders.GENERIC_AMOUNT, amount)
                .replace(QuestsPlaceholders.PLAYER_NAME, user.getName())
                .replace(data.replacePlaceholders())
            );
            plugin.getUserManager().save(user);
        });
        return true;
    }
}
