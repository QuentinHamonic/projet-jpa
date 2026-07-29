package fr.diginamic.dao;

import fr.diginamic.entities.Personne;
import jakarta.persistence.EntityManager;

/**
 * Accès aux données des personnes.
 */
public class PersonneDao extends GenericDao<Personne, String> {

    public PersonneDao(EntityManager entityManager) {
        super(entityManager, Personne.class);
    }
}
