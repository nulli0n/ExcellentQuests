package su.nightexpress.quests.task.workstation.impl;

import org.bukkit.block.Campfire;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.task.workstation.AbstractWorkstation;
import su.nightexpress.quests.task.workstation.WorkstationType;

public class CampfireWorkstation extends AbstractWorkstation<Campfire> {

    public CampfireWorkstation(@NotNull Campfire backend) {
        super(WorkstationType.CAMPFIRE, backend);
    }

    @Override
    public boolean isCrafting() {
        for (int slot = 0; slot < this.backend.getSize(); slot++) {
            if (this.backend.getCookTime(slot) > 0) {
                return true;
            }
        }
        return false;
    }
}
