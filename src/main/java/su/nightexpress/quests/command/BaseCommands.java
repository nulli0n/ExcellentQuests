package su.nightexpress.quests.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.config.Perms;

public class BaseCommands {

    public static void load(@NotNull QuestsPlugin plugin, @NotNull HubNodeBuilder builder) {
        builder.branch(Commands.literal("reload")
            .description(CoreLang.COMMAND_RELOAD_DESC)
            .permission(Perms.COMMAND_RELOAD)
            .executes((context, arguments) -> {
                plugin.doReload(context.getSender());
                return true;
            })
        );
    }
}
