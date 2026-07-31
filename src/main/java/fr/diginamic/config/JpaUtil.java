package fr.diginamic.config;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Point d'accès partagé à l'unité de persistance de l'application.
 */
public final class JpaUtil {

    private static final String DEFAULT_SCHEMA_MODE = "validate";
    private static final Set<String> ALLOWED_SCHEMA_MODES = Set.of("validate", "create");

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
            Persistence.createEntityManagerFactory("cinema", createPersistenceProperties());

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

    private static Map<String, Object> createPersistenceProperties() {
        Map<String, Object> properties = new HashMap<>();

        addEnvironmentOverride(properties, "jakarta.persistence.jdbc.url", "DB_URL");
        addEnvironmentOverride(properties, "jakarta.persistence.jdbc.user", "DB_USER");
        addEnvironmentOverride(properties, "jakarta.persistence.jdbc.password", "DB_PASSWORD");

        String schemaMode = System.getenv()
                .getOrDefault("HIBERNATE_DDL_AUTO", DEFAULT_SCHEMA_MODE)
                .trim()
                .toLowerCase(Locale.ROOT);

        if (!ALLOWED_SCHEMA_MODES.contains(schemaMode)) {
            throw new IllegalStateException(
                    "HIBERNATE_DDL_AUTO doit valoir 'validate' ou 'create'.");
        }

        properties.put("hibernate.hbm2ddl.auto", schemaMode);
        return properties;
    }

    private static void addEnvironmentOverride(
            Map<String, Object> properties,
            String persistenceProperty,
            String environmentVariable) {

        String value = System.getenv(environmentVariable);

        if (value != null && !value.isBlank()) {
            properties.put(persistenceProperty, value);
        }
    }
}
