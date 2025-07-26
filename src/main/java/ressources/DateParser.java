package ressources;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateParser {

    // Parse la date et retourne une String formatée en "yyyy-MM-dd"
    public static String parseDateToIsoString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;

        // Normaliser les séparateurs
        String normalized = dateStr.trim().replaceAll("[\\.\\-\\s]+", "/");

        // Essayer avec année à 4 chiffres
        DateTimeFormatter formatterFullYear = DateTimeFormatter.ofPattern("d/M/yyyy");
        try {
            LocalDate date = LocalDate.parse(normalized, formatterFullYear);
            return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            // Ignorer et tenter année sur 2 chiffres
        }

        // Essayer avec année à 2 chiffres, en supposant qu'on ajoute "20" devant
        DateTimeFormatter formatterShortYear = DateTimeFormatter.ofPattern("d/M/yy");
        try {
            LocalDate date = LocalDate.parse(normalized, formatterShortYear);
            // Si année à 2 chiffres (ex: "25"), Java suppose 2025
            return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static LocalDate parseStringToLocalDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;

        try {
            // Pas de normalisation : on suppose le format ISO yyyy-MM-dd
            return LocalDate.parse(dateStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}