package location.newLocation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateParser {
    public static LocalDate parseDate(String dateStr) {
        // Remplacer les séparateurs possibles par des slash par exemple
        String normalized = dateStr.trim().replaceAll("[\\.\\-\\s]", "/");

        // Format attendu : jour/mois/année (ex: 26/06/2025)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

        try {
            LocalDate date = LocalDate.parse(normalized, formatter);
            return date;
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String parseString(String dateStr) {
        // Remplacer les séparateurs possibles par des slash par exemple
        String normalized = dateStr.trim().replaceAll("[\\.\\-\\s]", "/");

        // Format attendu : jour/mois/année (ex: 26/06/2025)
        DateTimeFormatter inputformatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        DateTimeFormatter outputformatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            LocalDate date = LocalDate.parse(normalized, inputformatter);
            return date.format(outputformatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}