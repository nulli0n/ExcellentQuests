package su.nightexpress.quests.task.workstation.impl;

import org.bukkit.block.BrewingStand;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.task.workstation.AbstractWorkstation;
import su.nightexpress.quests.task.workstation.WorkstationType;

public class BrewingWorkstation extends AbstractWorkstation<BrewingStand> {

    public BrewingWorkstation(@NotNull BrewingStand backend) {
        super(WorkstationType.BREWING_STAND, backend);
    }

    @Override
    public boolean isCrafting() {
        return this.backend.getBrewingTime() > 0;
    }

    /*@Override
    public int getRemainingTime() {
        return this.backend.getBrewingTime();
    }

    @Override
    public void setRemainingTime(int remainingTime) {
        this.backend.setBrewingTime(remainingTime);
    }*/
}
