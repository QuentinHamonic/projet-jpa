package fr.diginamic.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Point d'accès partagé à l'unité de persistance de l'application.
 */
public final class JpaUtil {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
            Persistence.createEntityManagerFactory("cinema");

    private JpaUtil() {
    }

    /**
     * Crée un gestionnaire d'entités. L'appelant doit le fermer après utilisation.
     *
     * @return un nouveau gestionnaire d'entités
     */
    public static EntityManager createEntityManager() {
        return ENTITY_MANAGER_FACTORY.createEntityManager();
    }

    /**
     * Ferme la fabrique de gestionnaires d'entités à l'arrêt de l'application.
     */
    public static void close() {
        if (ENTITY_MANAGER_FACTORY.isOpen()) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }
}
