package fr.diginamic.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Vérifie la cohérence des méthodes utilitaires gérant les relations métier.
 */
public class EntityRelationshipTest {

    @Test
    public void shouldSynchronizeRoleOwners() {
        Film film = createFilm("tt0000001");
        Person person = createPerson("nm0000001");
        Role role = new Role();

        film.addRole(role);
        person.addRole(role);

        assertSame(film, role.getFilm());
        assertSame(person, role.getPerson());
        assertTrue(film.getRoles().contains(role));
        assertTrue(person.getRoles().contains(role));
    }

    @Test
    public void shouldNotAddTheSameRoleTwice() {
        Film film = createFilm("tt0000001");
        Person person = createPerson("nm0000001");
        Role role = new Role();

        film.addRole(role);
        film.addRole(role);
        person.addRole(role);
        person.addRole(role);

        assertEquals(1, film.getRoles().size());
        assertEquals(1, person.getRoles().size());
    }

    @Test
    public void shouldRemoveRoleFromBothOwners() {
        Film film = createFilm("tt0000001");
        Person person = createPerson("nm0000001");
        Role role = new Role();

        film.addRole(role);
        person.addRole(role);
        film.removeRole(role);
        person.removeRole(role);

        assertTrue(film.getRoles().isEmpty());
        assertTrue(person.getRoles().isEmpty());
        assertNull(role.getFilm());
        assertNull(role.getPerson());
    }

    @Test
    public void shouldDeduplicateDirectorsAndGenres() {
        Film film = createFilm("tt0000001");
        Person firstDirector = createPerson("nm0000001");
        Person duplicateDirector = createPerson("nm0000001");
        Genre firstGenre = createGenre("Drama");
        Genre duplicateGenre = createGenre("Drama");

        film.addDirector(firstDirector);
        film.addDirector(duplicateDirector);
        film.addDirector(null);
        film.addGenre(firstGenre);
        film.addGenre(duplicateGenre);
        film.addGenre(null);

        assertEquals(1, film.getDirectors().size());
        assertEquals(1, film.getGenres().size());
    }

    private static Film createFilm(String imdbId) {
        Film film = new Film();
        film.setImdbId(imdbId);
        return film;
    }

    private static Person createPerson(String imdbId) {
        Person person = new Person();
        person.setImdbId(imdbId);
        return person;
    }

    private static Genre createGenre(String name) {
        Genre genre = new Genre();
        genre.setName(name);
        return genre;
    }
}
