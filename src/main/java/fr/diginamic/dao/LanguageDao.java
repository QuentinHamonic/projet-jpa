package fr.diginamic.dao;

import fr.diginamic.entities.Language;
import jakarta.persistence.EntityManager;

import java.util.Optional;

/**
 * Accès aux données des langues.
 */
public class LanguageDao extends GenericDao<Language, Long> {

    public LanguageDao(EntityManager entityManager) {
        super(entityManager, Language.class);
    }

    /**
     * Recherche une langue par son nom.
     *
     * @param name nom recherché
     * @return langue éventuellement trouvée
     */
    public Optional<Language> findByName(String name) {
        return entityManager.createQuery("SELECT l FROM Language l WHERE l.name = :name", Language.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}
