package su.nightexpress.quests.api.event.battlepass.level;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.battlepass.data.BattlePassData;
import su.nightexpress.quests.user.QuestUser;

public class BattlePassLevelDownEvent extends BattlePassLevelEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public BattlePassLevelDownEvent(@NotNull Player player, @NotNull QuestUser user, @NotNull BattlePassData data, int oldLevel) {
        super(player, user, data, oldLevel);
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
