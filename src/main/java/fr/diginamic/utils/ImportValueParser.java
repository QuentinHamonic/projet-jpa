package fr.diginamic.utils;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convertit les valeurs textuelles du fichier JSON en valeurs métier.
 */
public final class ImportValueParser {

    private static final Pattern METRIC_HEIGHT_PATTERN = Pattern.compile("([0-9]+[.,][0-9]+)\\s*m");

    private static final DateTimeFormatter ENGLISH_DATE_FORMATTER = DateTimeFormatter
            .ofPattern("MMMM d uuuu", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FRENCH_DATE_FORMATTER = DateTimeFormatter
            .ofPattern("d MMMM uuuu", Locale.FRENCH)
            .withResolverStyle(ResolverStyle.STRICT);

    private ImportValueParser() {
    }

    /**
     * Nettoie les espaces d'un texte brut.
     *
     * @param text texte à nettoyer
     * @return texte nettoyé, ou {@code null} s'il est vide
     */
    public static String cleanText(String text) {
        if (text == null) {
            return null;
        }

        text = text.replace('\u00A0', ' ').replace('\u202F', ' ').trim();

        if (text.isEmpty()) {
            return null;
        }
        return text;
    }

    /**
     * Normalise un texte pour l'utiliser comme clé de dédoublonnage.
     *
     * @param text texte à normaliser
     * @return clé sans accent et en minuscules, ou {@code null}
     */
    public static String normalizeKey(String text) {
        text = cleanText(text);

        if (text == null) {
            return null;
        }

        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Convertit une note comprise entre zéro et dix.
     *
     * @param rating note brute
     * @return note convertie, ou {@code null} si elle est invalide
     */
    public static BigDecimal parseRating(String rating) {

        rating = cleanText(rating);
        if (rating == null) {
            return null;
        }

        rating = rating.replace(",", ".");

        try {

            BigDecimal decimalRating = new BigDecimal(rating);

            if (decimalRating.compareTo(BigDecimal.ZERO) < 0 || decimalRating.compareTo(BigDecimal.TEN) > 0) {
                return null;
            }
            return decimalRating;

        } catch (NumberFormatException exception) {
            return null;
        }

    }

    /**
     * Convertit une année ou conserve la borne minimale d'un intervalle.
     *
     * @param year année brute
     * @return année convertie, ou {@code null} si elle est invalide
     */
    public static Integer parseYear(String year) {
        year = cleanText(year);
        if (year == null) {
            return null;
        }

        int separatorIndex = year.indexOf('\u2013');

        if (separatorIndex >= 0) {
            year = cleanText(year.substring(0, separatorIndex));
        }

        try {
            Integer yearInt = Integer.parseInt(year);
            return yearInt;

        } catch (NumberFormatException exception) {
            return null;
        }

    }

    /**
     * Extrait une taille exprimée en mètres.
     *
     * @param height taille brute
     * @return taille convertie, ou {@code null} si elle est invalide
     */
    public static BigDecimal parseHeight(String height) {
        height = cleanText(height);
        if (height == null) {
            return null;
        }
        Matcher matcher = METRIC_HEIGHT_PATTERN.matcher(height);
        if (!matcher.find()) {
            return null;
        }

        try {
            BigDecimal heightBigDecimal = new BigDecimal(matcher.group(1).replace(",", "."));
            return heightBigDecimal;
        } catch (NumberFormatException exception) {
            return null;
        }

    }

    /**
     * Convertit une date complète écrite en anglais ou en français.
     *
     * @param date date brute
     * @return date convertie, ou {@code null} si elle est incomplète ou invalide
     */
    public static LocalDate parseDate(String date) {
        date = cleanText(date);
        if (date == null) {
            return null;
        }

        try {

            return LocalDate.parse(date, ENGLISH_DATE_FORMATTER);

        } catch (DateTimeParseException englishException) {

            try {

                return LocalDate.parse(date, FRENCH_DATE_FORMATTER);

            } catch (DateTimeParseException frenchException) {

                return null;
            }
        }
    }
}
