package su.nightexpress.quests.api.event.battlepass.season;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.battlepass.definition.BattlePassSeason;

public class BattlePassSeasonLaunchEvent extends BattlePassSeasonEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public BattlePassSeasonLaunchEvent(@NotNull BattlePassSeason season) {
        super(season);
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
