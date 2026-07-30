package fr.diginamic.dao;

import java.util.List;

import fr.diginamic.entities.Film;
import jakarta.persistence.EntityManager;

/**
 * Accès aux données des films.
 */
public class FilmDao extends GenericDao<Film, String> {

    public FilmDao(EntityManager entityManager) {
        super(entityManager, Film.class);
    }

    /**
     * Recherche la filmographie d'un acteur par son nom.
     *
     * @param actorName nom complet de l'acteur
     * @return films dans lesquels il joue
     */
    public List<Film> findByActorName(String actorName) {
        String jpql = """
                SELECT DISTINCT f
                FROM Film f
                JOIN f.roles r
                JOIN r.person p
                WHERE LOWER(p.name) = LOWER(:actorName)
                ORDER BY f.releaseYear, f.title
                """;

        return entityManager.createQuery(jpql, Film.class)
                .setParameter("actorName", actorName)
                .getResultList();
    }

    /**
     * Recherche les films sortis entre deux années incluses.
     *
     * @param startYear première année de la période
     * @param endYear   dernière année de la période
     * @return films de la période triés par année puis par titre
     */
    public List<Film> findByReleaseYearBetween(int startYear, int endYear) {
        String jpql = """
                SELECT f
                FROM Film f
                WHERE f.releaseYear BETWEEN :startYear AND :endYear
                ORDER BY f.releaseYear, f.title
                """;

        return entityManager.createQuery(jpql, Film.class)
                .setParameter("startYear", startYear)
                .setParameter("endYear", endYear)
                .getResultList();
    }

    /**
     * Recherche les films d'un acteur sortis entre deux années incluses.
     *
     * @param actorName nom complet de l'acteur
     * @param startYear première année de la période
     * @param endYear   dernière année de la période
     * @return films correspondants triés par année puis par titre
     */
    public List<Film> findByActorNameAndReleaseYearBetween(
            String actorName,
            int startYear,
            int endYear) {

        String jpql = """
                SELECT DISTINCT f
                FROM Film f
                JOIN f.roles r
                JOIN r.person p
                WHERE LOWER(p.name) = LOWER(:actorName)
                  AND f.releaseYear BETWEEN :startYear AND :endYear
                ORDER BY f.releaseYear, f.title
                """;

        return entityManager.createQuery(jpql, Film.class)
                .setParameter("actorName", actorName)
                .setParameter("startYear", startYear)
                .setParameter("endYear", endYear)
                .getResultList();
    }

    /**
     * Recherche les films dans lesquels jouent deux acteurs donnés.
     *
     * @param firstActorName  nom complet du premier acteur
     * @param secondActorName nom complet du second acteur
     * @return films communs aux deux acteurs, triés par année puis par titre
     */
    public List<Film> findCommonByActorNames(
            String firstActorName,
            String secondActorName) {

        String jpql = """
                SELECT f
                FROM Film f
                JOIN f.roles r
                JOIN r.person p
                WHERE LOWER(p.name) IN (
                    LOWER(:firstActorName),
                    LOWER(:secondActorName)
                )
                GROUP BY f
                HAVING COUNT(DISTINCT p.name) = 2
                ORDER BY f.releaseYear ASC, f.title
                """;

        return entityManager.createQuery(jpql, Film.class)
                .setParameter("firstActorName", firstActorName)
                .setParameter("secondActorName", secondActorName)
                .getResultList();
    }
}
