package fr.diginamic.dao;

import fr.diginamic.entities.BirthPlace;
import jakarta.persistence.EntityManager;

import java.util.Optional;

/**
 * Accès aux données des lieux de naissance.
 */
public class BirthPlaceDao extends GenericDao<BirthPlace, Long> {

    public BirthPlaceDao(EntityManager entityManager) {
        super(entityManager, BirthPlace.class);
    }

    /**
     * Recherche un lieu de naissance par son libellé.
     *
     * @param label libellé recherché
     * @return lieu éventuellement trouvé
     */
    public Optional<BirthPlace> findByLabel(String label) {
        return entityManager.createQuery(
                        "SELECT b FROM BirthPlace b WHERE b.label = :label",
                        BirthPlace.class)
                .setParameter("label", label)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}
