package fr.diginamic.dao;

import fr.diginamic.entities.Langue;
import jakarta.persistence.EntityManager;

import java.util.Optional;

/**
 * Accès aux données des langues.
 */
public class LangueDao extends GenericDao<Langue, Long> {

    public LangueDao(EntityManager entityManager) {
        super(entityManager, Langue.class);
    }

    /**
     * Recherche une langue par son nom.
     *
     * @param nom nom recherché
     * @return langue éventuellement trouvée
     */
    public Optional<Langue> findByNom(String nom) {
        return entityManager.createQuery("SELECT l FROM Langue l WHERE l.nom = :nom", Langue.class)
                .setParameter("nom", nom)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}
