package su.nightexpress.quests.api.exception;

import org.jetbrains.annotations.NotNull;

public class QuestLoadException extends RuntimeException {

    public QuestLoadException(@NotNull String message) {
        super(message);
    }
}
