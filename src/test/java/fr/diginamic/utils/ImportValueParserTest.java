package fr.diginamic.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Test;

/**
 * Vérifie la conversion des valeurs textuelles provenant du fichier JSON.
 */
public class ImportValueParserTest {

    @Test
    public void shouldReturnNullForMissingText() {
        assertNull(ImportValueParser.cleanText(null));
        assertNull(ImportValueParser.cleanText(""));
        assertNull(ImportValueParser.cleanText("  \u00A0\u202F  "));
    }

    @Test
    public void shouldCleanSupportedSpaces() {
        assertEquals("Montréal", ImportValueParser.cleanText(" \u00A0Montréal\u202F "));
    }

    @Test
    public void shouldNormalizeDeduplicationKeys() {
        assertEquals("montreal", ImportValueParser.normalizeKey(" Montréal "));
        assertEquals("ete", ImportValueParser.normalizeKey("ÉTÉ"));
        assertNull(ImportValueParser.normalizeKey("   "));
    }

    @Test
    public void shouldParseValidRatings() {
        assertEquals(new BigDecimal("6.3"), ImportValueParser.parseRating("6,3"));
        assertEquals(new BigDecimal("8.5"), ImportValueParser.parseRating(" 8.5 "));
        assertEquals(BigDecimal.ZERO, ImportValueParser.parseRating("0"));
        assertEquals(BigDecimal.TEN, ImportValueParser.parseRating("10"));
    }

    @Test
    public void shouldRejectInvalidRatings() {
        assertNull(ImportValueParser.parseRating("-0.1"));
        assertNull(ImportValueParser.parseRating("10.1"));
        assertNull(ImportValueParser.parseRating("inconnue"));
        assertNull(ImportValueParser.parseRating(" "));
    }

    @Test
    public void shouldParseReleaseYears() {
        assertEquals(Integer.valueOf(1981), ImportValueParser.parseYear(" 1981 "));
        assertEquals(Integer.valueOf(1969), ImportValueParser.parseYear("1969–1970"));
    }

    @Test
    public void shouldRejectInvalidYears() {
        assertNull(ImportValueParser.parseYear(null));
        assertNull(ImportValueParser.parseYear("inconnue"));
        assertNull(ImportValueParser.parseYear(" "));
    }

    @Test
    public void shouldParseMetricHeights() {
        assertEquals(new BigDecimal("1.75"), ImportValueParser.parseHeight("1,75 m"));
        assertEquals(new BigDecimal("1.78"), ImportValueParser.parseHeight("5' 10\" (1.78 m)"));
    }

    @Test
    public void shouldRejectInvalidHeights() {
        assertNull(ImportValueParser.parseHeight(null));
        assertNull(ImportValueParser.parseHeight("5' 10\""));
        assertNull(ImportValueParser.parseHeight("inconnue"));
    }

    @Test
    public void shouldParseCompleteDates() {
        assertEquals(LocalDate.of(1930, 3, 24), ImportValueParser.parseDate("March 24 1930"));
        assertEquals(LocalDate.of(1930, 3, 24), ImportValueParser.parseDate("24 mars 1930"));
    }

    @Test
    public void shouldRejectInvalidDates() {
        assertNull(ImportValueParser.parseDate(null));
        assertNull(ImportValueParser.parseDate("1930"));
        assertNull(ImportValueParser.parseDate("February 30 1930"));
    }
}
