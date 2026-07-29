package fr.diginamic.dao;

import fr.diginamic.entities.Pays;
import jakarta.persistence.EntityManager;

import java.util.Optional;

/**
 * Accès aux données des pays.
 */
public class PaysDao extends GenericDao<Pays, Long> {

    public PaysDao(EntityManager entityManager) {
        super(entityManager, Pays.class);
    }

    /**
     * Recherche un pays par son nom.
     *
     * @param nom nom recherché
     * @return pays éventuellement trouvé
     */
    public Optional<Pays> findByNom(String nom) {
        return entityManager.createQuery("SELECT p FROM Pays p WHERE p.nom = :nom", Pays.class)
                .setParameter("nom", nom)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}
