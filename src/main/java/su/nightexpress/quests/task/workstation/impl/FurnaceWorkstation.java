package su.nightexpress.quests.task.workstation.impl;

import org.bukkit.block.Furnace;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.task.workstation.AbstractWorkstation;
import su.nightexpress.quests.task.workstation.WorkstationType;

public class FurnaceWorkstation extends AbstractWorkstation<Furnace> {

    public FurnaceWorkstation(@NotNull WorkstationType type, @NotNull Furnace backend) {
        super(type, backend);
    }

    @Override
    public boolean isCrafting() {
        return this.backend.getCookTime() > 0;
    }

    /*@Override
    public int getRemainingTime() {
        return this.backend.getCookTimeTotal() - this.backend.getCookTime();
    }

    @Override
    public void setRemainingTime(int remainingTime) {
        this.backend.setCookTime((short) (this.backend.getCookTimeTotal() - remainingTime));
    }*/
}
