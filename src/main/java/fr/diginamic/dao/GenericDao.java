package fr.diginamic.dao;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * Fournit les opérations communes d'accès aux entités JPA.
 *
 * @param <E> type de l'entité
 * @param <PK> type de sa clé primaire
 */
public abstract class GenericDao<E, PK> {

    protected final EntityManager entityManager;
    private final Class<E> entityClass;

    /**
     * Crée un DAO pour un type d'entité.
     *
     * @param entityManager gestionnaire d'entités
     * @param entityClass classe de l'entité
     */
    protected GenericDao(EntityManager entityManager, Class<E> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    /**
     * Recherche une entité par sa clé primaire.
     *
     * @param id clé primaire recherchée
     * @return entité éventuellement trouvée
     */
    public Optional<E> findById(PK id) {

        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    /**
     * Rend une nouvelle entité persistante.
     *
     * @param e entité à persister
     * @return entité devenue persistante
     */
    public E persist(E e) {
        entityManager.persist(e);
        return e;
    }

    /**
     * Met à jour une entité et renvoie l'instance gérée.
     *
     * @param e entité à mettre à jour
     * @return instance gérée par JPA
     */
    public E update(E e) {
        return entityManager.merge(e);
    }

    /**
     * Supprime une entité à partir de sa clé primaire.
     *
     * @param id clé primaire de l'entité
     */
    public void deleteById(PK id) {
        Optional<E> entity = findById(id);
        entity.ifPresent(entityManager::remove);
    }

    /**
     * Renvoie toutes les entités du type géré.
     *
     * @return liste des entités
     */
    public List<E> findAll() {
        String entityName = entityClass.getSimpleName();
        TypedQuery<E> q = entityManager.createQuery("Select e From " + entityName + " e", entityClass);
        return q.getResultList();
    }
}
