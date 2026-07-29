package fr.diginamic.dao;

import fr.diginamic.entities.Film;
import jakarta.persistence.EntityManager;

/**
 * Accès aux données des films.
 */
public class FilmDao extends GenericDao<Film, String> {

    public FilmDao(EntityManager entityManager) {
        super(entityManager, Film.class);
    }
}
