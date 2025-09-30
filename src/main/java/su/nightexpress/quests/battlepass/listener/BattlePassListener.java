package su.nightexpress.quests.battlepass.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.battlepass.BattlePassManager;

public class BattlePassListener extends AbstractListener<QuestsPlugin> {

    private final BattlePassManager manager;

    public BattlePassListener(@NotNull QuestsPlugin plugin, @NotNull BattlePassManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        this.manager.checkPremium(event.getPlayer());
    }
}
