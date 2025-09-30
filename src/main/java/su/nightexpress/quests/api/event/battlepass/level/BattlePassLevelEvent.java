package su.nightexpress.quests.api.event.battlepass.level;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.battlepass.data.BattlePassData;
import su.nightexpress.quests.user.QuestUser;

public abstract class BattlePassLevelEvent extends Event {

    protected final Player player;
    protected final QuestUser user;
    protected final BattlePassData data;
    protected final int oldLevel;

    public BattlePassLevelEvent(@NotNull Player player, @NotNull QuestUser user, @NotNull BattlePassData data, int oldLevel) {
        this.player = player;
        this.user = user;
        this.data = data;
        this.oldLevel = oldLevel;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @NotNull
    public QuestUser getUser() {
        return this.user;
    }

    @NotNull
    public BattlePassData getData() {
        return this.data;
    }

    public int getOldLevel() {
        return this.oldLevel;
    }
}
