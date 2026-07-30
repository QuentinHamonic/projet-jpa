package fr.diginamic.services;

import static org.junit.Assert.assertThrows;

import org.junit.Test;

/**
 * Vérifie les règles de validation appliquées avant les recherches en base.
 */
public class SearchServiceTest {

    private final SearchService searchService = new SearchService(null);

    @Test
    public void shouldRejectBlankSearchTerms() {
        assertThrows(
                IllegalArgumentException.class,
                () -> searchService.findFilmography("  \u00A0 "));
    }

    @Test
    public void shouldRejectIdenticalActorNames() {
        assertThrows(
                IllegalArgumentException.class,
                () -> searchService.findCommonFilms("Steve McQueen", "steve mcqueen"));
    }

    @Test
    public void shouldRejectIdenticalFilmTitles() {
        assertThrows(
                IllegalArgumentException.class,
                () -> searchService.findCommonActors("Film", " film "));
    }

    @Test
    public void shouldRejectReversedYearRange() {
        assertThrows(
                IllegalArgumentException.class,
                () -> searchService.findFilmsBetweenYears(2020, 2010));
    }
}
