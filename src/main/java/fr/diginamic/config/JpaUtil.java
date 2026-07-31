package fr.diginamic.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    private static final String DEFAULT_JDBC_URL =
            "jdbc:mysql://localhost:3306/cinema?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Paris";
    private static final String DEFAULT_JDBC_USER = "root";
    private static final String DEFAULT_SCHEMA_MODE = "validate";
    private static final Set<String> ALLOWED_SCHEMA_MODES =
            Set.of("validate", "create", "create-only");

    private static EntityManagerFactory entityManagerFactory;
    private static String activeSchemaMode;

    private JpaUtil() {
    }

    /**
     * Crée un gestionnaire d'entités. L'appelant doit le fermer après utilisation.
     *
     * @return un nouveau gestionnaire d'entités
     */
    public static EntityManager createEntityManager() {
        return createEntityManager(resolveSchemaMode());
    }

    /**
     * Crée un gestionnaire d'entités avec l'action de schéma demandée.
     *
     * @param schemaMode action Hibernate appliquée à la création de la fabrique
     * @return un nouveau gestionnaire d'entités
     */
    public static synchronized EntityManager createEntityManager(String schemaMode) {
        String normalizedMode = normalizeSchemaMode(schemaMode);

        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            entityManagerFactory = Persistence.createEntityManagerFactory(
                    "cinema",
                    createPersistenceProperties(normalizedMode));
            activeSchemaMode = normalizedMode;
        } else if (!activeSchemaMode.equals(normalizedMode)) {
            throw new IllegalStateException(
                    "Fermez la fabrique JPA avant de changer le mode de schéma.");
        }

        return entityManagerFactory.createEntityManager();
    }

    /**
     * Ferme la fabrique de gestionnaires d'entités à l'arrêt de l'application.
     */
    public static synchronized void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }

        entityManagerFactory = null;
        activeSchemaMode = null;
    }

    /**
     * Vérifie si la table principale du schéma existe.
     *
     * @return {@code true} si la table {@code film} existe
     * @throws SQLException si la base ne peut pas être interrogée
     */
    public static boolean schemaExists() throws SQLException {
        try (Connection connection = createJdbcConnection();
                ResultSet tables = connection.getMetaData().getTables(
                        connection.getCatalog(),
                        null,
                        "film",
                        new String[]{"TABLE"})) {

            return tables.next();
        }
    }

    /**
     * Vérifie si au moins un film a déjà été importé.
     *
     * @return {@code true} si la table {@code film} contient une ligne
     * @throws SQLException si la base ne peut pas être interrogée
     */
    public static boolean containsFilms() throws SQLException {
        try (Connection connection = createJdbcConnection();
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery("SELECT 1 FROM film LIMIT 1")) {

            return result.next();
        }
    }

    private static Connection createJdbcConnection() throws SQLException {
        return DriverManager.getConnection(
                environmentValue("DB_URL", DEFAULT_JDBC_URL),
                environmentValue("DB_USER", DEFAULT_JDBC_USER),
                environmentValue("DB_PASSWORD", ""));
    }

    private static Map<String, Object> createPersistenceProperties(String schemaMode) {
        Map<String, Object> properties = new HashMap<>();

        properties.put("jakarta.persistence.jdbc.url", environmentValue("DB_URL", DEFAULT_JDBC_URL));
        properties.put("jakarta.persistence.jdbc.user", environmentValue("DB_USER", DEFAULT_JDBC_USER));
        properties.put("jakarta.persistence.jdbc.password", environmentValue("DB_PASSWORD", ""));
        properties.put("hibernate.hbm2ddl.auto", schemaMode);
        return properties;
    }

    private static String resolveSchemaMode() {
        return normalizeSchemaMode(
                environmentValue("HIBERNATE_DDL_AUTO", DEFAULT_SCHEMA_MODE));
    }

    private static String normalizeSchemaMode(String schemaMode) {
        String normalizedMode = schemaMode.trim().toLowerCase(Locale.ROOT);

        if (!ALLOWED_SCHEMA_MODES.contains(normalizedMode)) {
            throw new IllegalArgumentException(
                    "Le mode de schéma doit valoir 'validate', 'create' ou 'create-only'.");
        }

        return normalizedMode;
    }

    private static String environmentValue(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
