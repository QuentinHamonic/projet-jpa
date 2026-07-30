package fr.diginamic.dao;

import fr.diginamic.entities.Role;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * Accès aux données des rôles.
 */
public class RoleDao extends GenericDao<Role, Long> {

    public RoleDao(EntityManager entityManager) {
        super(entityManager, Role.class);
    }

    /**
     * Recherche le rôle d'une personne dans un film.
     *
     * @param filmId   identifiant IMDb du film
     * @param personId identifiant IMDb de la personne
     * @return rôle éventuellement trouvé
     */
    public Optional<Role> findByFilmAndPerson(String filmId, String personId) {
        String jpql = """
                SELECT r
                FROM Role r
                WHERE r.film.imdbId = :filmId
                AND r.person.imdbId = :personId
                """;
        return entityManager.createQuery(jpql, Role.class)
                .setParameter("filmId", filmId)
                .setParameter("personId", personId)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    /**
     * Recherche le casting d'un film par son titre.
     *
     * @param filmTitle titre complet du film
     * @return rôles du film, casting principal en premier
     */
    public List<Role> findCastByFilmTitle(String filmTitle) {
        String jpql = """
                SELECT r
                FROM Role r
                JOIN FETCH r.person p
                JOIN r.film f
                WHERE LOWER(f.title) = LOWER(:filmTitle)
                ORDER BY r.mainCast DESC, p.name
                """;
        return entityManager.createQuery(jpql, Role.class)
                .setParameter("filmTitle", filmTitle)
                .getResultList();
    }
}
