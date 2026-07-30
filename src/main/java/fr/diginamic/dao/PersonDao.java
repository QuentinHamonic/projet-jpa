package fr.diginamic.dao;

import fr.diginamic.entities.Person;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Accès aux données des personnes.
 */
public class PersonDao extends GenericDao<Person, String> {

    public PersonDao(EntityManager entityManager) {
        super(entityManager, Person.class);
    }

    /**
     * Recherche les acteurs présents dans deux films.
     *
     * @param firstFilmTitle titre du premier film
     * @param secondFilmTitle titre du second film
     * @return acteurs communs triés par nom
     */
    public List<Person> findCommonActorsByFilmTitles(
            String firstFilmTitle,
            String secondFilmTitle) {

        String jpql = """
                SELECT p
                FROM Person p
                JOIN p.roles r
                JOIN r.film f
                WHERE LOWER(f.title) IN (
                    LOWER(:firstFilmTitle),
                    LOWER(:secondFilmTitle)
                )
                GROUP BY p
                HAVING COUNT(DISTINCT f.title) = 2
                ORDER BY p.name
                """;

        return entityManager.createQuery(jpql, Person.class)
                .setParameter("firstFilmTitle", firstFilmTitle)
                .setParameter("secondFilmTitle", secondFilmTitle)
                .getResultList();
    }
}
