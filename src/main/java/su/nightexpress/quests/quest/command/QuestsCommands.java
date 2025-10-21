package su.nightexpress.quests.quest.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.command.NightCommand;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.quests.QuestsPlaceholders;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.config.Lang;
import su.nightexpress.quests.config.Perms;
import su.nightexpress.quests.quest.QuestManager;

public class QuestsCommands {

    public static final String DEFAULT_ALIAS = "quests";

    private static final String ARG_PLAYER = "player";

    private static QuestsPlugin plugin;
    private static QuestManager manager;
    private static NightCommand command;

    public static void load(@NotNull QuestsPlugin questsPlugin, @NotNull QuestManager questManager) {
        plugin = questsPlugin;
        manager = questManager;

        command = NightCommand.hub(plugin, Config.FEATURES_QUESTS_ALIASES.get(), builder -> builder
            .localized(Lang.COMMAND_QUESTS_NAME)
            .permission(Perms.COMMAND_QUESTS)
            .description(Lang.COMMAND_QUESTS_DESC)
            .branch(Commands.literal("refresh")
                .permission(Perms.COMMAND_QUESTS_REFRESH)
                .description(Lang.COMMAND_QUESTS_REFRESH_DESC)
                .withArguments(Arguments.playerName(ARG_PLAYER))
                .executes(QuestsCommands::refreshQuests)
            )
            .executes(QuestsCommands::openQuests)
        );
        command.register();
    }

    public static void shutdown() {
        command.unregister();
        command = null;
        manager = null;
        plugin = null;
    }

    private static boolean openQuests(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        if (!context.isPlayer()) {
            context.errorPlayerOnly();
            return false;
        }

        Player player = context.getPlayerOrThrow();
        manager.openQuests(player);
        return true;
    }

    private static boolean refreshQuests(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String playerName = arguments.getString(ARG_PLAYER);
        plugin.getUserManager().manageUser(playerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            Player player = user.getPlayer();
            if (player != null) {
                manager.refreshQuests(player);
            }
            else {
                user.setNewQuestsDate(0L);
                plugin.getUserManager().save(user);
            }
            context.send(Lang.QUESTS_REFRESHED_FOR, replacer -> replacer.replace(QuestsPlaceholders.PLAYER_NAME, user.getName()));
        });
        return true;
    }
}
