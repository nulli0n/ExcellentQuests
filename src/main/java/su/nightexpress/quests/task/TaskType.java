package su.nightexpress.quests.task;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.task.adapter.AdapterFamily;

public class TaskType<O, F extends AdapterFamily<O>> {

    private final String id;
    private final F      adapterFamily;

    public TaskType(@NotNull String id, F adapterFamily) {
        this.id = id;
        this.adapterFamily = adapterFamily;
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public F getAdapterFamily() {
        return this.adapterFamily;
    }
}
