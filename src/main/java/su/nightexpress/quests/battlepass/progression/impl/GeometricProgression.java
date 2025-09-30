package su.nightexpress.quests.battlepass.progression.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.quests.battlepass.progression.Progression;

public class GeometricProgression implements Progression, Writeable {

    //private final int startLevel;
    //private final int startXP;
    private final int          initialXP;
    private final double       xpFactor;
    private final int          maxLevel;

    public GeometricProgression(/*int startLevel, int startXP, */int initialXP, double xpFactor, int maxLevel) {
        //this.startLevel = startLevel;
        //this.startXP = startXP;
        this.initialXP = initialXP;
        this.xpFactor = xpFactor;
        this.maxLevel = maxLevel;
    }

    @NotNull
    public static GeometricProgression read(@NotNull FileConfig config, @NotNull String path) {
        //int startLevel = ConfigValue.create(path + ".StartLevel", 1).read(config);
        //int startXP = ConfigValue.create(path + ".StartXP", 0).read(config);
        int initialXP = ConfigValue.create(path + ".InitialXP", Progression.DEFAULT_INITIAL_XP).read(config);
        double xpFactor = ConfigValue.create(path + ".XP_Factor", Progression.DEFAULT_XP_SCALE).read(config);
        int maxLevel = ConfigValue.create(path + ".Max_Level", Progression.DEFAULT_MAX_LEVEL).read(config);

        return new GeometricProgression(/*startLevel, startXP, */initialXP, xpFactor, maxLevel);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        //config.set(path + ".StartLevel", this.startLevel);
        //config.set(path + ".StartXP", this.startXP);
        config.set(path + ".InitialXP", this.initialXP);
        config.set(path + ".XP_Factor", this.xpFactor);
        config.set(path + ".Max_Level", this.maxLevel);
    }

    @Override
    public boolean canProgress() {
        return this.maxLevel > 0;//this.startLevel;
    }

    @Override
    public int getXPToLevel(int level) {
        return (int) (this.initialXP * (Math.pow(this.xpFactor, level)));
    }

    /*@Override
    public int getStartLevel() {
        return this.startLevel;
    }

    @Override
    public int getStartXP() {
        return this.startXP;
    }*/

    @Override
    public int getInitialXP() {
        return this.initialXP;
    }

    @Override
    public double getXPFactor() {
        return this.xpFactor;
    }

    @Override
    public int getMaxLevel() {
        return this.maxLevel;
    }
}
