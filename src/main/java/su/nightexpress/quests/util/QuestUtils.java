package su.nightexpress.quests.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.quests.config.Config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class QuestUtils {

    private static DateTimeFormatter dateTimeFormatter;

    public static void setDateTimeFormatter(@NotNull String string) {
        QuestUtils.dateTimeFormatter = DateTimeFormatter.ofPattern(string);
    }

    @NotNull
    public static String formatDateTime(@NotNull LocalDateTime dateTime) {
        return dateTime.format(dateTimeFormatter);
    }

    @NotNull
    public static String formatDateTime(long date) {
        return formatDateTime(TimeUtil.getLocalDateTimeOf(date));
    }

    public static boolean isIntegrationAvailable(@NotNull String pluginName) {
        return Plugins.isInstalled(pluginName) && !Config.INTERGRATIONS_DISABLED.get().contains(pluginName);
    }

    @NotNull
    public static LocalDateTime getNewDayMidnight() {
        LocalDate date = TimeUtil.getCurrentDate().plusDays(1);
        LocalTime time = LocalTime.MIDNIGHT;

        return LocalDateTime.of(date, time);
    }
}
