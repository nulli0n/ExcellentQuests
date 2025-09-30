package su.nightexpress.quests.milestone.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.milestone.MilestoneManager;

public class MilestoneGenericListener extends AbstractListener<QuestsPlugin> {

    private final MilestoneManager manager;

    public MilestoneGenericListener(@NotNull QuestsPlugin plugin, @NotNull MilestoneManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        this.manager.updateMilestones(event.getPlayer());
    }
}
