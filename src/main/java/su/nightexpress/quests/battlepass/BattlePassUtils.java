package su.nightexpress.quests.battlepass;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.battlepass.config.BattlePassConfig;
import su.nightexpress.quests.battlepass.definition.BattlePassSeason;
import su.nightexpress.quests.battlepass.definition.BattlePassType;
import su.nightexpress.quests.config.Perms;
import su.nightexpress.quests.user.QuestUser;

public class BattlePassUtils {

    public static boolean hasPremium(@NotNull Player player, @NotNull QuestUser user, @NotNull BattlePassSeason season) {
        return user.getBattlePassData(season).isPremium() || (BattlePassConfig.SETTINGS_PREMIUM_BY_PERMISSION.get() && player.hasPermission(Perms.BATTLE_PASS_PREMIUM));
    }

    @NotNull
    public static BattlePassType getPassType(@NotNull Player player, @NotNull QuestUser user, @NotNull BattlePassSeason season) {
        return hasPremium(player, user, season) ? BattlePassType.PREMIUM : BattlePassType.FREE;
    }
}
