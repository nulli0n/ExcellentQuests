package su.nightexpress.quests.battlepass.progression;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.battlepass.progression.impl.DummyProgression;
import su.nightexpress.quests.battlepass.progression.impl.GeometricProgression;

public interface Progression {

    int    START_LEVEL        = 0;
    int    DEFAULT_MAX_LEVEL  = 105;
    int    DEFAULT_INITIAL_XP = 250;
    double DEFAULT_XP_SCALE   = 1.00669;

    @NotNull
    static GeometricProgression getDefault() {
        return new GeometricProgression(Progression.DEFAULT_INITIAL_XP, Progression.DEFAULT_XP_SCALE, Progression.DEFAULT_MAX_LEVEL);
    }

    @NotNull
    static DummyProgression noProgression() {
        return new DummyProgression();
    }

    boolean canProgress();

    int getXPToLevel(int level);

    int getInitialXP();

    double getXPFactor();

    int getMaxLevel();
}
