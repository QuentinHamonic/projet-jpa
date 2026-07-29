package fr.diginamic.dao;

import java.util.Optional;

import fr.diginamic.entities.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * Accès aux données des genres.
 */
public class GenreDao extends GenericDao<Genre, Long> {

    public GenreDao(EntityManager entityManager) {
        super(entityManager, Genre.class);
    }

    /**
     * Recherche un genre par son nom.
     *
     * @param name nom recherché
     * @return genre éventuellement trouvé
     */
    public Optional<Genre> findByName(String name) {
        TypedQuery<Genre> q = entityManager.createQuery("SELECT g FROM Genre g WHERE g.name = :name", Genre.class);
        q.setParameter("name", name);
        q.setMaxResults(1);
        return q.getResultStream().findFirst();
    }
}
