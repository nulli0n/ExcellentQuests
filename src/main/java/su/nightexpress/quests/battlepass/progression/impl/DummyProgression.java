package su.nightexpress.quests.battlepass.progression.impl;

import su.nightexpress.quests.battlepass.progression.Progression;

public class DummyProgression implements Progression {

    @Override
    public boolean canProgress() {
        return false;
    }

    /*@Override
    public int getStartLevel() {
        return 0;
    }

    @Override
    public int getStartXP() {
        return 0;
    }*/

    @Override
    public int getXPToLevel(int level) {
        return 0;
    }

    @Override
    public int getInitialXP() {
        return 0;
    }

    @Override
    public double getXPFactor() {
        return 0;
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }
}
