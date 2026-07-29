package fr.diginamic.dao;

import fr.diginamic.entities.Role;
import jakarta.persistence.EntityManager;

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
     * @param filmId identifiant IMDb du film
     * @param personId identifiant IMDb de la personne
     * @return rôle éventuellement trouvé
     */
    public Optional<Role> findByFilmAndPerson(String filmId, String personId) {
        return entityManager.createQuery("""
                        SELECT r
                        FROM Role r
                        WHERE r.film.imdbId = :filmId
                          AND r.person.imdbId = :personId
                        """, Role.class)
                .setParameter("filmId", filmId)
                .setParameter("personId", personId)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}
