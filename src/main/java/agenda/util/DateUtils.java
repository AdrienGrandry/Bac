package agenda.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class DateUtils {
    // Retourne le premier lundi avant ou égal à la date donnée (utile pour afficher la grille complète)
    public static LocalDate getStartMonday(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
}