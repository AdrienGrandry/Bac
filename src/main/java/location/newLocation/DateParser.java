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
            System.out.println("Format de date invalide: " + dateStr);
            return null;
        }
    }

    public static void main(String[] args) {
        String[] testDates = {
                "31-02-2025",
                "31-2-2025",
                "31.02.2025",
                "31.2.2025",
                "31 02 2025",
                "31 2 2025",
                "31/02/2025",
                "31/2/2025"
        };

        for (String d : testDates) {
            LocalDate date = parseDate(d);
            System.out.println(d + " -> " + date);
        }
    }
}