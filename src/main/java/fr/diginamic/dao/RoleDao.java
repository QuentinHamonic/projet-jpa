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
     * @param personneId identifiant IMDb de la personne
     * @return rôle éventuellement trouvé
     */
    public Optional<Role> findByFilmAndPersonne(String filmId, String personneId) {
        return entityManager.createQuery("""
                        SELECT r
                        FROM Role r
                        WHERE r.film.idImdb = :filmId
                          AND r.personne.idImdb = :personneId
                        """, Role.class)
                .setParameter("filmId", filmId)
                .setParameter("personneId", personneId)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}
