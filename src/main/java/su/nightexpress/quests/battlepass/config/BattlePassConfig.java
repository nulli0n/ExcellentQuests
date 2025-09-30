package su.nightexpress.quests.battlepass.config;

import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.quests.battlepass.progression.Progression;
import su.nightexpress.quests.battlepass.progression.impl.GeometricProgression;
import su.nightexpress.quests.config.Perms;

public class BattlePassConfig {

    public static final ConfigValue<Boolean> SETTINGS_PREMIUM_BY_PERMISSION = ConfigValue.create("Settings.GivePremiumByPermission",
        true,
        "Whether to unlock the Premium Pass for players with the '" + Perms.BATTLE_PASS_PREMIUM.getName() + "' permission."
    );

    public static final ConfigValue<Progression> LEVELING_PROGRESSION = ConfigValue.create("Leveling.Progression",
        GeometricProgression::read,
        Progression.getDefault(),
        "Leveling settings for the Battle Pass."
    );

    public static int getMaxLevel() {
        return LEVELING_PROGRESSION.get().getMaxLevel();
    }
}
