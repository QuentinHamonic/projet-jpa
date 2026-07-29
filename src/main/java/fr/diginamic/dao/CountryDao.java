package fr.diginamic.dao;

import fr.diginamic.entities.Country;
import jakarta.persistence.EntityManager;

import java.util.Optional;

/**
 * Accès aux données des pays.
 */
public class CountryDao extends GenericDao<Country, Long> {

    public CountryDao(EntityManager entityManager) {
        super(entityManager, Country.class);
    }

    /**
     * Recherche un pays par son nom.
     *
     * @param name nom recherché
     * @return pays éventuellement trouvé
     */
    public Optional<Country> findByName(String name) {
        return entityManager.createQuery("SELECT c FROM Country c WHERE c.name = :name", Country.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}
