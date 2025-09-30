package su.nightexpress.quests.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.quests.QuestsPlaceholders;

public class Perms {

    public static final String PREFIX         = "excellentquests.";
    public static final String PREFIX_COMMAND = PREFIX + "command.";
    public static final String PREFIX_BYPASS  = PREFIX + "bypass";

    public static final UniPermission PLUGIN  = new UniPermission(PREFIX + QuestsPlaceholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + QuestsPlaceholders.WILDCARD);
    public static final UniPermission BYPASS  = new UniPermission(PREFIX_BYPASS + QuestsPlaceholders.WILDCARD);

    public static final UniPermission BATTLE_PASS_PREMIUM = new UniPermission(PREFIX + "battlepass.premium");

    public static final UniPermission COMMAND_RELOAD = new UniPermission(PREFIX_COMMAND + "reload");

    public static final UniPermission COMMAND_QUESTS         = new UniPermission(PREFIX_COMMAND + "quests");
    public static final UniPermission COMMAND_QUESTS_REFRESH = new UniPermission(PREFIX_COMMAND + "quests.refresh");

    public static final UniPermission COMMAND_MILESTONES       = new UniPermission(PREFIX_COMMAND + "milestones");
    public static final UniPermission COMMAND_MILESTONES_RESET = new UniPermission(PREFIX_COMMAND + "milestones.reset");

    public static final UniPermission COMMAND_BATTLE_PASS        = new UniPermission(PREFIX_COMMAND + "battlepass");
    //public static final UniPermission COMMAND_BATTLE_PASS_OPEN        = new UniPermission(PREFIX_COMMAND + "battlepass.open");
    //public static final UniPermission COMMAND_BATTLE_PASS_OPEN_OTHERS = new UniPermission(PREFIX_COMMAND + "battlepass.open.others");
    public static final UniPermission COMMAND_BATTLE_PASS_START  = new UniPermission(PREFIX_COMMAND + "battlepass.start");
    public static final UniPermission COMMAND_BATTLE_PASS_CANCEL = new UniPermission(PREFIX_COMMAND + "battlepass.cancel");

    public static final UniPermission COMMAND_BATTLE_PASS_ADD_LEVEL     = new UniPermission(PREFIX_COMMAND + "battlepass.addlevel");
    public static final UniPermission COMMAND_BATTLE_PASS_REMOVE_LEVEL  = new UniPermission(PREFIX_COMMAND + "battlepass.removelevel");
    public static final UniPermission COMMAND_BATTLE_PASS_SET_LEVEL     = new UniPermission(PREFIX_COMMAND + "battlepass.setlevel");
    public static final UniPermission COMMAND_BATTLE_PASS_ADD_XP        = new UniPermission(PREFIX_COMMAND + "battlepass.addxp");
    public static final UniPermission COMMAND_BATTLE_PASS_REMOVE_XP     = new UniPermission(PREFIX_COMMAND + "battlepass.removexp");
    public static final UniPermission COMMAND_BATTLE_PASS_SET_XP        = new UniPermission(PREFIX_COMMAND + "battlepass.setxp");
    
    static {
        PLUGIN.addChildren(
            BATTLE_PASS_PREMIUM,
            COMMAND,
            BYPASS
        );

        COMMAND.addChildren(
            COMMAND_RELOAD,
            COMMAND_QUESTS,
            COMMAND_QUESTS_REFRESH,

            COMMAND_MILESTONES,
            COMMAND_MILESTONES_RESET,

            COMMAND_BATTLE_PASS,
            //COMMAND_BATTLE_PASS_OPEN,
            //COMMAND_BATTLE_PASS_OPEN_OTHERS,
            COMMAND_BATTLE_PASS_START,
            COMMAND_BATTLE_PASS_CANCEL,

            COMMAND_BATTLE_PASS_ADD_LEVEL,
            COMMAND_BATTLE_PASS_REMOVE_LEVEL,
            COMMAND_BATTLE_PASS_SET_LEVEL,
            COMMAND_BATTLE_PASS_ADD_XP,
            COMMAND_BATTLE_PASS_REMOVE_XP,
            COMMAND_BATTLE_PASS_SET_XP
        );
    }
}
