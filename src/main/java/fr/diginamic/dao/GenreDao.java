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
     * @param nom nom recherché
     * @return genre éventuellement trouvé
     */
    public Optional<Genre> findByNom(String nom) {
        TypedQuery<Genre> q = entityManager.createQuery("Select g From Genre g where g.nom = :nom", Genre.class);
        q.setParameter("nom", nom);
        q.setMaxResults(1);
        return q.getResultStream().findFirst();
    }
}
