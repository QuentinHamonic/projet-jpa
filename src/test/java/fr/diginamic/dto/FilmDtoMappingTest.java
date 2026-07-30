package fr.diginamic.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Vérifie la correspondance entre la structure JSON et les DTO d'import.
 */
public class FilmDtoMappingTest {

    /**
     * Vérifie le mapping des propriétés simples, imbriquées et collectionnées d'un film.
     *
     * @throws IOException si le JSON de test ne peut pas être lu
     */
    @Test
    public void shouldMapCompleteFilmJson() throws IOException {
        String json = """
                {
                  "id": "tt0000001",
                  "pays": {"nom": "France", "url": "https://example.test/france"},
                  "nom": "Film de test",
                  "url": "https://example.test/film",
                  "rating": "6,3",
                  "plot": "Résumé de test",
                  "langue": "Français",
                  "lieuTournage": {
                    "ville": "Paris",
                    "etatDept": "Île-de-France",
                    "pays": "France"
                  },
                  "realisateurs": [
                    {"id": "nm0000001", "identite": "Réalisateur Test"}
                  ],
                  "castingPrincipal": [
                    {"id": "nm0000002", "identite": "Acteur Test"}
                  ],
                  "anneeSortie": "1979–1980",
                  "roles": [
                    {
                      "characterName": "Personnage Test",
                      "acteur": {"id": "nm0000002", "identite": "Acteur Test"}
                    }
                  ],
                  "genres": ["Drama"]
                }
                """;

        FilmDto film = new ObjectMapper().readValue(json, FilmDto.class);

        assertEquals("tt0000001", film.getImdbId());
        assertEquals("Film de test", film.getTitle());
        assertEquals("1979–1980", film.getReleaseYear());
        assertEquals("France", film.getCountry().getName());
        assertEquals("Paris", film.getFilmingLocation().getCity());
        assertEquals("Réalisateur Test", film.getDirectors().get(0).getName());
        assertEquals("Acteur Test", film.getMainCast().get(0).getName());
        assertEquals("Personnage Test", film.getRoles().get(0).getCharacterName());
        assertEquals("Drama", film.getGenres().get(0));
    }

    /**
     * Vérifie que les collections sont initialisées lorsque les propriétés sont absentes.
     *
     * @throws IOException si le JSON de test ne peut pas être lu
     */
    @Test
    public void shouldKeepCollectionsInitializedWhenMissing() throws IOException {
        FilmDto film = new ObjectMapper().readValue(
                "{\"id\":\"tt0000001\",\"nom\":\"Film de test\"}",
                FilmDto.class);

        assertNotNull(film.getDirectors());
        assertNotNull(film.getMainCast());
        assertNotNull(film.getRoles());
        assertNotNull(film.getGenres());
        assertTrue(film.getDirectors().isEmpty());
        assertTrue(film.getMainCast().isEmpty());
        assertTrue(film.getRoles().isEmpty());
        assertTrue(film.getGenres().isEmpty());
    }
}
