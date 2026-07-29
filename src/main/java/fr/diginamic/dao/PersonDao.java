package fr.diginamic.dao;

import fr.diginamic.entities.Person;
import jakarta.persistence.EntityManager;

/**
 * Accès aux données des personnes.
 */
public class PersonDao extends GenericDao<Person, String> {

    public PersonDao(EntityManager entityManager) {
        super(entityManager, Person.class);
    }
}
