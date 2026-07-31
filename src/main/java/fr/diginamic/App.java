package fr.diginamic;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.diginamic.config.JpaUtil;
import fr.diginamic.services.ImportService;
import jakarta.persistence.EntityManager;

/**
 * Point d'entrée de l'application cinéma.
 */
public final class App {

    private App() {
    }

    /**
     * Initialise les données au premier lancement puis démarre les recherches.
     *
     * @param args arguments de la ligne de commande
     * @throws IOException si la lecture du fichier JSON échoue
     * @throws SQLException si l'état de la base ne peut pas être vérifié
     */
    public static void main(String[] args) throws IOException, SQLException {
        configureFrameworkLogging();
        initializeDatabaseIfNecessary();
        openSearchMenu();
    }

    private static void configureFrameworkLogging() {
        Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
    }

    private static void initializeDatabaseIfNecessary() throws IOException, SQLException {
        boolean schemaExists = JpaUtil.schemaExists();

        if (schemaExists && JpaUtil.containsFilms()) {
            return;
        }

        String schemaMode = schemaExists ? "create" : "create-only";
        System.out.println("Initialisation de la base de données…");

        EntityManager entityManager = JpaUtil.createEntityManager(schemaMode);

        try {
            new ImportService(entityManager).importFilms();
            long filmCount = entityManager
                    .createQuery("SELECT COUNT(f) FROM Film f", Long.class)
                    .getSingleResult();

            System.out.printf("%d films importés. Base prête.%n", filmCount);
        } finally {
            closeEntityManager(entityManager);
            JpaUtil.close();
        }
    }

    private static void openSearchMenu() {
        EntityManager entityManager = JpaUtil.createEntityManager("validate");

        try {
            SearchApp.run(entityManager);
        } finally {
            closeEntityManager(entityManager);
            JpaUtil.close();
        }
    }

    private static void closeEntityManager(EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
