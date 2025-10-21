package su.nightexpress.quests.milestone.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.ArgumentNodeBuilder;
import su.nightexpress.nightcore.commands.command.NightCommand;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.commands.exceptions.CommandSyntaxException;
import su.nightexpress.quests.QuestsPlaceholders;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.config.Lang;
import su.nightexpress.quests.config.Perms;
import su.nightexpress.quests.milestone.MilestoneManager;
import su.nightexpress.quests.milestone.definition.Milestone;

import java.util.ArrayList;
import java.util.Optional;

public class MilestoneCommands {

    public static final String DEFAULT_ALIAS = "milestones";

    private static final String ARG_PLAYER    = "player";
    private static final String ARG_MILESTONE = "milestone";

    private static QuestsPlugin     plugin;
    private static MilestoneManager manager;
    private static NightCommand     command;

    public static void load(@NotNull QuestsPlugin questsPlugin, @NotNull MilestoneManager milestoneManager) {
        plugin = questsPlugin;
        manager = milestoneManager;

        command = NightCommand.hub(plugin, Config.FEATURES_MILESTONES_ALIASES.get(), builder -> builder
            .localized(Lang.COMMAND_MILESTONES_NAME)
            .permission(Perms.COMMAND_MILESTONES)
            .description(Lang.COMMAND_MILESTONES_DESC)
            .branch(Commands.literal("reset")
                .permission(Perms.COMMAND_MILESTONES_RESET)
                .description(Lang.COMMAND_MILESTONES_RESET_DESC)
                .withArguments(
                    Arguments.playerName(ARG_PLAYER),
                    milestoneArgument()
                )
                .executes(MilestoneCommands::resetMilestone)
            )
            .executes(MilestoneCommands::openCategories)
        );
        command.register();
    }

    @NotNull
    private static ArgumentNodeBuilder<Milestone> milestoneArgument() {
        return Commands.argument(ARG_MILESTONE, (contextBuilder, string) -> Optional.ofNullable(manager.getMilestoneById(string)).orElseThrow(() -> CommandSyntaxException.custom(Lang.COMMAND_SYNTAX_INVALID_MILESTONE)))
            .localized(Lang.COMMAND_ARGUMENT_NAME_MILESTONE)
            .suggestions((reader, context) -> new ArrayList<>(manager.getMilestoneByIdMap().keySet()));
    }

    public static void shutdown() {
        command.unregister();
        command = null;
        manager = null;
        plugin = null;
    }

    private static boolean openCategories(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        if (!context.isPlayer()) {
            context.errorPlayerOnly();
            return false;
        }

        Player player = context.getPlayerOrThrow();
        manager.openCategories(player);
        return true;
    }

    private static boolean resetMilestone(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String playerName = arguments.getString(ARG_PLAYER);
        Milestone milestone = arguments.get(ARG_MILESTONE, Milestone.class);
        plugin.getUserManager().manageUser(playerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            user.getMilestoneData(milestone).reset();
            plugin.getUserManager().save(user);
            context.send(Lang.MILESTONES_RESET_FOR, replacer -> replacer.replace(milestone.replacePlaceholders()).replace(QuestsPlaceholders.PLAYER_NAME, user.getName()));
        });
        return true;
    }
}
