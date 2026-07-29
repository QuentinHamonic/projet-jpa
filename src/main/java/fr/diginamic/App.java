package fr.diginamic;

import java.io.IOException;

import fr.diginamic.config.JpaUtil;
import fr.diginamic.services.ImportService;
import jakarta.persistence.EntityManager;

/**
 * Point d'entrée de l'import des films.
 */
public class App {

    /**
     * Lance l'import puis ferme les ressources JPA.
     *
     * @param args arguments de la ligne de commande
     * @throws IOException si la lecture du fichier JSON échoue
     */
    public static void main(String[] args) throws IOException {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            ImportService importService = new ImportService(entityManager);
            importService.importFilms();

            System.out.println("Import terminé.");
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }

            JpaUtil.close();
        }
    }
}
