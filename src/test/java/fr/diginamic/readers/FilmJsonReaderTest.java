package fr.diginamic.readers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import fr.diginamic.dto.FilmDto;

/**
 * Vérifie la lecture en streaming de la ressource cinématographique complète.
 */
public class FilmJsonReaderTest {

    /**
     * Vérifie que chaque objet du tableau JSON est transmis une seule fois au consommateur.
     *
     * @throws IOException si la ressource ne peut pas être lue
     */
    @Test
    public void shouldStreamEveryFilmFromResource() throws IOException {
        AtomicInteger filmCount = new AtomicInteger();
        AtomicReference<FilmDto> firstFilm = new AtomicReference<>();

        new FilmJsonReader().read(film -> {
            firstFilm.compareAndSet(null, film);
            filmCount.incrementAndGet();
        });

        assertEquals(2_748, filmCount.get());
        assertNotNull(firstFilm.get());
        assertNotNull(firstFilm.get().getImdbId());
        assertNotNull(firstFilm.get().getTitle());
    }
}
