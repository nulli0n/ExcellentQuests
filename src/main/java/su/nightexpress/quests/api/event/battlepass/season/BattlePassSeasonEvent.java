package su.nightexpress.quests.api.event.battlepass.season;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.battlepass.definition.BattlePassSeason;

public abstract class BattlePassSeasonEvent extends Event {

    protected final BattlePassSeason season;

    public BattlePassSeasonEvent(@NotNull BattlePassSeason season) {
        this.season = season;
    }
}
